package org.holoeverywhere.resbuilder.types

import org.gradle.api.Project
import org.holoeverywhere.resbuilder.api.Type
import org.holoeverywhere.resbuilder.tasks.ResbuilderDefaultTask

import java.util.regex.Pattern

class TypeDrawable implements Type {
    private static final Pattern XML_DRAWABLE_PATTERN = Pattern.compile("@(android:)?drawable/([a-zA-Z0-9_]+)")
    private static final int XML_DRAWABLE_PATTERN_NAME = 2

    @Override
    String getName() {
        return "drawable"
    }

    private class TypeDrawableState {
        def Set<String> grab = new HashSet<>()
        def File output
    }

    @Override
    TypeDrawableState process(Map<String, ?> data, File output, Object stateRaw, File input) {
        TypeDrawableState state = stateRaw == null ? new TypeDrawableState() : stateRaw as TypeDrawableState;
        state.output = output
        data.get('grab')?.each { String grab ->
            state.grab.add(grab)
        }
        return state
    }

    @Override
    void flush(Object stateRaw) {
        if (stateRaw == null) return
        TypeDrawableState state = stateRaw as TypeDrawableState
        Set<File> effectiveGrab = new HashSet<>()
        if (state.grab != null) {
            buildEffectiveGrabModel(effectiveGrab, state.grab)
        }
        effectiveGrab.each { File file ->
            File drawableDir = new File(state.output, file.parentFile.name)
            if (!drawableDir.exists()) drawableDir.mkdirs()
            File targetFile = new File(drawableDir, file.name)
            if (file.name.endsWith(".xml")) {
                targetFile.withWriter("utf-8") { writer ->
                    def boolean ignoreComment = false
                    file.readLines("utf-8").each { String line ->
                        String trimmed = line.trim()
                        if (!ignoreComment && trimmed.startsWith("<!--") && !trimmed.endsWith("-->")) {
                            ignoreComment = true
                        } else if (ignoreComment && trimmed.endsWith("-->")) {
                            ignoreComment = false
                        } else if (!ignoreComment && !trimmed.endsWith("-->") && line.trim().length() > 0) {
                            writer.write(line.replaceAll(XML_DRAWABLE_PATTERN, "@drawable/\$${XML_DRAWABLE_PATTERN_NAME}"))
                            writer.write('\n')
                        }
                    }
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

    def buildEffectiveGrabModel(Set<File> effectiveModel, Set<String> data) {
        Set<File> subset = new HashSet<>()
        data.each { String grab ->
            project.fileTree(resourcesDir) {
                include "drawable/${grab}.xml"
                include "drawable-*/${grab}.png"
                include "drawable-*/${grab}.9.png"
            }.each { File file ->
                subset.add(file)
            }
        }
        effectiveModel.addAll(subset)
        Set<String> xmlSubset = new HashSet<>()
        subset.findAll { it.name.endsWith(".xml") }.each { File file ->
            file.readLines("utf-8").each {
                it.eachMatch(XML_DRAWABLE_PATTERN) {
                    xmlSubset.add(it[XML_DRAWABLE_PATTERN_NAME])
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
