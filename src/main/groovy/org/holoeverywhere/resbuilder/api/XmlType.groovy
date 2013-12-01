package org.holoeverywhere.resbuilder.api

import groovy.xml.MarkupBuilder

abstract class XmlType extends PathType<XmlState> {
    @Override
    def process(Map<String, ?> data, XmlState state, File input) {
        return process(data, state, state.markup, input)
    }

    abstract void process(Map<String, ?> data, XmlState state, MarkupBuilder markup, File input)

    @Override
    XmlState createState(File path) {
        return new XmlState(path, 'utf-8')
    }

    public static class XmlState implements PathType.IPathState {
        def MarkupBuilder markup
        def Writer writer

        XmlState(File path, String encoding) {
            final File parentFile = path.getParentFile()
            if (!parentFile.exists()) parentFile.mkdirs()
            writer = new OutputStreamWriter(new FileOutputStream(path), encoding)
            markup = new MarkupBuilder(writer)
            markup.mkp.xmlDeclaration(version: '1.0', encoding: encoding)
        }

        @Override
        void flush() throws IOException {
            writer.flush()
            writer.close()
            writer = null
            markup = null
        }

        @Override
        void prepare() {

        }
    }
}
