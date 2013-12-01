package org.holoeverywhere.resbuilder.api

abstract class AndroidXmlType extends XmlType {
    final String name, filename

    AndroidXmlType(String name) {
        this(name, "rb_${name}.xml")
    }

    AndroidXmlType(String name, String filename) {
        this.name = name
        this.filename = filename
    }

    @Override
    String getName() {
        return name
    }

    XmlType.XmlState obtainState(String modifiers, Closure<?> action) {
        return obtainState(obtainPath(output, modifiers), action)
    }

    @Override
    File obtainPath(File output, Map<String, ?> data, File input) {
        return obtainPath(output, data.get('modifiers', '').toString().replace(' ', '-'))
    }

    def File obtainPath(File output, String modifiers) {
        final String pathDir = 'values' + (modifiers != null && modifiers.length() > 0 ? "-${modifiers}" : '')
        return new File(output, pathDir + File.separator + filename)
    }
}
