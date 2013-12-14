package org.holoeverywhere.resbuilder.types

import com.android.build.gradle.BasePlugin
import com.android.sdklib.IAndroidTarget
import groovy.xml.MarkupBuilder
import org.gradle.api.Project
import org.holoeverywhere.resbuilder.api.AndroidXmlType
import org.holoeverywhere.resbuilder.api.XmlType
import org.holoeverywhere.resbuilder.tasks.ResbuilderDefaultTask

class TypeStrings extends AndroidXmlType {
    static class StringsState extends XmlType.XmlState {
        private final TypeStrings type

        StringsState(File path, String encoding, TypeStrings type) {
            super(path, encoding)
            this.type = type
        }

        @Override
        void prepare() {
            grab.each { String grabName ->
                type.parsedData.entrySet().findAll { it.value.containsKey(grabName) }.each {
                    type.obtainState(it.key, { StringsState state ->
                        if (!state.strings.containsKey(grabName)) {
                            state.strings.put(grabName, it.value.get(grabName))
                        }
                    })
                }
            }
        }

        @Override
        void flush() {
            markup.resources() {
                strings.sort().each {
                    markup.string(name: it.key, it.value)
                }
            }
            super.flush()
        }

        def Set<String> grab = new HashSet<>()
        def Map<String, String> strings = new HashMap<>()
    }

    TypeStrings() {
        super('strings')
    }

    @Override
    StringsState createState(File path) {
        return new StringsState(path, 'utf-8', this)
    }

    private boolean parsed = false
    private Map<String, Map<String, String>> parsedData = new HashMap<>()

    @Override
    void process(Map<String, ?> data, XmlType.XmlState stateRaw, MarkupBuilder markup, File input) {
        final StringsState state = stateRaw as StringsState

        List<String> grab = data.get('grab') as List<String>
        if (grab != null && grab.size() > 0) {
            if (!parsed) {
                parsed = true
                parse()
            }
            state.grab.addAll(grab)
        }

        data.get('strings')?.each { String key, String value ->
            state.strings.put(key, value)
        }
    }

    def void parse() {
        final XmlParser parser = new XmlParser()
        project.fileTree(resourcesDir) {
            include 'values/strings.xml'
            include 'values-*/strings.xml'
        }.each { File file ->
            String modifiers = file.getParentFile().getName()
            int i = modifiers.indexOf('-')
            modifiers = i > 0 ? modifiers.substring(i + 1) : ''
            Map<String, String> data = new HashMap<>()
            parsedData.put(modifiers, data)

            final Node node = parser.parse(file)
            node.children().each { Node child ->
                if (child.name() == 'string') {
                    List childs = child.value() as List
                    if (childs.size() > 0) {
                        Object o = childs.first()
                        data.put(child.attribute('name') as String, o instanceof Node ? o.text() : o as String)
                    }
                }
            }
        }
    }

    private File resourcesDir
    private Project project

    @Override
    void bind(ResbuilderDefaultTask task) {
        project = task.project
        resourcesDir = task.obtainResourcesDir()
    }
}
