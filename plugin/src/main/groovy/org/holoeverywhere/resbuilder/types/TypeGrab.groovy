package org.holoeverywhere.resbuilder.types

import org.gradle.api.Project
import org.holoeverywhere.resbuilder.api.Type
import org.holoeverywhere.resbuilder.tasks.ResbuilderDefaultTask

import java.util.regex.Pattern

class TypeGrab implements Type {
    private static
    final Pattern XML_RESOURCE_PATTERN = Pattern.compile("(@|\\?)(android:)?(\\+)?([a-zA-Z]+)/([a-zA-Z0-9_]+)")
    private static final int XML_RESOURCE_PATTERN_RESTYPE = 1
    private static final int XML_RESOURCE_PATTERN_NAMESPACE = 2
    private static final int XML_RESOURCE_PATTERN_ADD = 3
    private static final int XML_RESOURCE_PATTERN_TYPE = 4
    private static final int XML_RESOURCE_PATTERN_NAME = 5

    @Override
    String getName() {
        return "grab"
    }

    private class TypeGrabState {
        def Map<String, Set<String>> grab = new HashMap<>()
        def Map<String, String> replaceTokens = new HashMap<>()
        def File output
    }

    @Override
    TypeGrabState process(Map<String, ?> data, File output, Object stateRaw, File input) {
        TypeGrabState state = stateRaw as TypeGrabState ?: new TypeGrabState()
        state.output = output
        data.get('grab')?.each { String type, List<String> values ->
            Set<String> grabValues = state.grab.get(type)
            if (grabValues == null) {
                state.grab.put(type, grabValues = new HashSet<>())
            }
            grabValues.addAll(values)
        }
        data.get('tokens')?.each { String from, String to ->
            state.replaceTokens.put(from, to)
        }
        return state
    }

    @Override
    void flush(Object stateRaw) {
        if (stateRaw == null) return
        TypeGrabState state = stateRaw as TypeGrabState
        Set<File> effectiveGrab = new HashSet<>()
        if (state.grab != null) {
            buildEffectiveGrabModel(effectiveGrab, state.grab)
        }
        effectiveGrab.each { File file ->
            File drawableDir = new File(state.output, file.parentFile.name)
            if (!drawableDir.exists()) drawableDir.mkdirs()
            File targetFile = new File(drawableDir, file.name)
            if (file.name.endsWith(".xml")) {
                Writer writer = new StringWriter()
                def boolean ignoreComment = false
                file.readLines("utf-8").each { String line ->
                    String trimmed = line.trim()
                    if (!ignoreComment && trimmed.startsWith("<!--") && !trimmed.endsWith("-->")) {
                        ignoreComment = true
                    } else if (ignoreComment && trimmed.endsWith("-->")) {
                        ignoreComment = false
                    } else if (!ignoreComment && !trimmed.endsWith("-->") && trimmed.length() > 0) {
                        writer.write(line)
                        writer.write('\n')
                    }
                }
                String xml = writer.toString()
                xml = xml.replaceAll(XML_RESOURCE_PATTERN,
                        "\$${XML_RESOURCE_PATTERN_RESTYPE}\$${XML_RESOURCE_PATTERN_ADD}\$${XML_RESOURCE_PATTERN_TYPE}/\$${XML_RESOURCE_PATTERN_NAME}")
                state.replaceTokens.each { String from, String to ->
                    xml = xml.replaceAll(from, to)
                }
                final Node data = new XmlParser().parse(new StringReader(xml))
                targetFile.withPrintWriter("utf-8") { PrintWriter fileWriter ->
                    XmlNodePrinter printer = new XmlNodePrinter(fileWriter)
                    printer.namespaceAware = true
                    printer.print(data)
                }
            } else {
                copyFile(file, targetFile)
            }
        }
    }

    def static copyFile(File source, File destination) {
        destination.withDataOutputStream {
            os -> source.withDataInputStream { is -> os << is }
        }
    }

    def buildEffectiveGrabModel(Set<File> effectiveModel, Map<String, Set<String>> data) {
        Set<File> subset = new HashSet<>()
        data.each { String type, Set<String> values ->
            values.each { String grab ->
                project.fileTree(resourcesDir) {
                    include "${type}/${grab}.*"
                    include "${type}-*/${grab}.*"
                }.each { File file ->
                    subset.add(file)
                }
            }
        }
        effectiveModel.addAll(subset)
        Map<String, Set<String>> xmlSubset = new HashMap<>()
        subset.findAll { it.name.endsWith(".xml") }.each { File file ->
            file.readLines("utf-8").each {
                it.eachMatch(XML_RESOURCE_PATTERN) {
                    if (it[XML_RESOURCE_PATTERN_NAMESPACE] != 'android:' && it[XML_RESOURCE_PATTERN_NAMESPACE] != null) {
                        project.logger.warn("Unknown namespace found \"${it[XML_RESOURCE_PATTERN_NAMESPACE]}\", file \"${file.absolutePath}\", trying to grab...")
                    }

                    def String type = it[XML_RESOURCE_PATTERN_TYPE]

                    Set<String> set = xmlSubset.get(type)
                    if (set == null) {
                        xmlSubset.put(type, set = new HashSet<>())
                    }
                    set.add(it[XML_RESOURCE_PATTERN_NAME])
                }
            }
        }
        if (xmlSubset.size() > 0) {
            buildEffectiveGrabModel(effectiveModel, xmlSubset)
        }
    }
    def Project project
    def File resourcesDir

    @Override
    void bind(ResbuilderDefaultTask task) {
        project = task.project
        resourcesDir = task.obtainResourcesDir()
    }
}
