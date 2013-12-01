package org.holoeverywhere.resbuilder.types

import groovy.xml.MarkupBuilder
import org.holoeverywhere.resbuilder.api.AndroidXmlType
import org.holoeverywhere.resbuilder.api.XmlType

class TypeStyles extends AndroidXmlType {
    static class StylesState extends XmlType.XmlState {
        StylesState(File path, String encoding) {
            super(path, encoding)
        }

        def Map<BlockNameHeader, Map<String, String>> blocks = new HashMap<>()
        def Map<StyleNameHeader, Map<String, String>> styles = new HashMap<>()

        @Override
        void prepare() {
            markup.resources() {
                styles.sort(StyleNameHeader.COMPARATOR).each { key, value ->
                    markup.style(name: key.name, parent: formatParent(key.parent, key.name)) {
                        buildData(value, key.blocks).sort().each { itemName, itemValue ->
                            markup.item(name: itemName, formatValue(itemValue))
                        }
                    }
                }
            }
        }

        static def String formatValue(Object object) {
            if (object == null) return ''
            String s = object.toString()
            if (s.charAt(0) == ':') {
                s = s.substring(1)
                try {
                    if (s.length() > 1 && s.length() <= 8) {
                        Integer.parseInt(s)
                        return "#$s"
                    }
                } catch (NumberFormatException e) {
                }
                return "@$s"
            }
            return s
        }

        static def String formatParent(String parent, String name) {
            if (parent != null && parent.length() > 0) return parent
            final int parentPoint = name.lastIndexOf('.')
            return parentPoint > 0 ? name.substring(0, parentPoint) : ''
        }

        def Map<String, String> buildData(Map<String, String> data, String[] blocks) {
            Map<String, String> result = new HashMap<>()
            blocks?.each {
                def entry = findBlockByName(it)
                if (entry == null) return
                result.putAll(buildData(entry.value, entry.key.parent))
            }
            if (data != null) {
                result.putAll(data)
            }
            return result
        }

        def Map.Entry<BlockNameHeader, Map<String, String>> findBlockByName(String name) {
            return blocks.find { it.key.name == name }
        }

        def Map.Entry<StyleNameHeader, Map<String, String>> findStyleByName(String name) {
            return styles.find { it.key.name == name }
        }
    }

    static class StyleNameHeader {
        public static final Comparator<StyleNameHeader> COMPARATOR = new Comparator<StyleNameHeader>() {
            @Override
            int compare(StyleNameHeader o1, StyleNameHeader o2) {
                return o1?.name?.compareTo(o2.name)
            }
        }

        def String name
        def String[] blocks
        def String parent

        // Format: Name | Block1 Block2 Block3 < Parent
        static StyleNameHeader parse(String s) {
            StyleNameHeader header = new StyleNameHeader()
            int parentPoint = s.lastIndexOf('<')
            if (parentPoint > 0) {
                header.parent = s.substring(parentPoint + 1).trim()
                s = s.substring(0, parentPoint)
            }
            int blocksPoint = s.indexOf('|')
            if (blocksPoint > 0) {
                header.blocks = s.substring(blocksPoint + 1).split(' ').findAll { it.length() > 0 }
                s = s.substring(0, blocksPoint)
            }
            header.name = s.trim()
            return header
        }
    }

    static class BlockNameHeader {
        def String name
        def String[] parent

        // Format: Name < Block1 Block2 Block3
        static BlockNameHeader parse(String s) {
            BlockNameHeader header = new BlockNameHeader()
            int parentPoint = s.lastIndexOf('<')
            if (parentPoint > 0) {
                header.parent = s.substring(parentPoint + 1).split(' ').findAll { it.length() > 0 }
                s = s.substring(0, parentPoint)
            }
            header.name = s.trim()
            return header
        }
    }

    TypeStyles() {
        super('styles')
    }

    @Override
    StylesState createState(File path) {
        return new StylesState(path, 'utf-8')
    }

    @Override
    void process(Map<String, ?> data, XmlType.XmlState stateRaw, MarkupBuilder markup, File input) {
        final StylesState state = stateRaw as StylesState

        data.get('blocks')?.each { String blockName, Map<String, String> blockData ->
            BlockNameHeader blockNameHeader = BlockNameHeader.parse(blockName)
            BlockNameHeader stateBlockNameHeader = state.findBlockByName(blockNameHeader.name)?.key
            if (stateBlockNameHeader != null) {
                state.blocks.remove(stateBlockNameHeader)
            }
            state.blocks.put(blockNameHeader, blockData)
        }

        data.get('styles')?.each { String styleName, Map<String, String> styleData ->
            StyleNameHeader styleNameHeader = StyleNameHeader.parse(styleName)
            StyleNameHeader stateStyleNameHeader = state.findStyleByName(styleNameHeader.name)?.key
            if (stateStyleNameHeader != null) {
                state.styles.remove(stateStyleNameHeader)
            }
            state.styles.put(styleNameHeader, styleData)
        }
    }
}
