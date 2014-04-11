package org.holoeverywhere.resbuilder.types

import groovy.xml.MarkupBuilder
import org.holoeverywhere.resbuilder.api.AndroidXmlType
import org.holoeverywhere.resbuilder.api.XmlType

class TypeAttrs extends AndroidXmlType {
    class AttrsState extends XmlType.XmlState {
        AttrsState(File path, String encoding) {
            super(path, encoding)
        }

        def Map<String, AttrNode> attrs = new HashMap<>()
        def Map<String, List<String>> styleable = new HashMap<>()

        @Override
        void prepare() {
            markup.resources() {
                attrs.sort().each { String name, AttrNode node ->
                    markup.attr(name: node.formatName(name), format: node.formatFormat()) {
                        def String nodeType = node.types.contains('flag') ? 'flag' : 'enum'
                        node.enums?.sort()?.each { int value, String enumName ->
                            markup."$nodeType"(name: enumName, value: value)
                        }
                    }
                }
                styleable.sort().each { String name, List<String> nodes ->
                    markup.'declare-styleable'(name: name) {
                        nodes.sort().each { String attrName ->
                            markup.attr(name: attrName)
                        }
                    }
                }
            }
        }
    }

    static class AttrNode {
        def String[] types
        def Map<Integer, String> enums

        // Format: type1:type2:type3|enumName1 1:enumName2 2:enumName3 3
        static AttrNode parse(String s) {
            AttrNode node = new AttrNode()
            int enumPoint = s.lastIndexOf('|')
            if (enumPoint > 0) {
                node.enums = new HashMap<>()
                s.substring(enumPoint + 1).split(':')*.split(' ').each { it ->
                    node.enums.put(it[1] as int, it[0] as String)
                }
                s = s.substring(0, enumPoint)
            }
            node.types = s.split(':')
            return node
        }

        def String formatName(String name) {
            return name ?: enums != null ? 'enum' : ''
        }

        // Yep. Format format
        def String formatFormat() {
            return types.join('|')
        }
    }

    TypeAttrs() {
        super('attrs')
    }

    @Override
    AttrsState createState(File path) {
        return new AttrsState(path, 'utf-8')
    }

    @Override
    void process(Map<String, ?> data, XmlType.XmlState stateRaw, MarkupBuilder markup, File input) {
        final AttrsState state = stateRaw as AttrsState
        data.get('attrs')?.each { String name, String value ->
            state.attrs.put(name, AttrNode.parse(value))
        }
        data.get('styleable')?.each { String name, List<String> value ->
            List<String> styleableBlockData = new ArrayList<>()
            value.each { String row ->
                String rowName
                int i = row.indexOf('|')
                if (i > 0) {
                    rowName = row.substring(0, i)
                    state.attrs.put(rowName, AttrNode.parse(row.substring(i + 1)))
                } else {
                    rowName = row
                }
                styleableBlockData.add(rowName)
            }
            state.styleable.put(name, styleableBlockData)
        }
    }
}
