
package org.holoeverywhere.translator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Processer {
    public static final class ProcesserState {
        public OutputStream os;
        public StringWriter stringWriter;
        public XMLStreamWriter writer;
    }

    private static final Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    private static final String DEFAULT_LOCALE = "en";
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

    private static ProcesserState createWriter(File outputDir, String locale, String filenamePattern)
            throws FileNotFoundException, XMLStreamException {
        File dir, file;
        if (locale.equals(DEFAULT_LOCALE)) {
            dir = new File(outputDir, "values");
        } else {
            // en_US -> en-rUS
            dir = new File(outputDir, "values-" + locale.replace("_", "-r"));
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file = new File(dir, String.format(filenamePattern, "strings"));
        ProcesserState state = new ProcesserState();
        state.os = new FileOutputStream(file);
        state.stringWriter = new StringWriter();
        state.writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(
                state.stringWriter);
        state.writer.writeStartDocument("utf-8", "1.0");
        state.writer.writeStartElement("resources");
        return state;
    }

    public static void process(Document document, Grabber grabber, File outputDir) {
        final Map<String, ProcesserState> writers = new HashMap<String, ProcesserState>();
        final SortedMap<String, Map<String, String>> data = new TreeMap<String, Map<String, String>>(
                COMPARATOR);
        data.putAll(document.mergeData(grabber));
        for (Entry<String, Map<String, String>> entry : data.entrySet()) {
            final String name = document.getNameForEntry(entry.getKey());
            try {
                for (Entry<String, String> translates : entry.getValue().entrySet()) {
                    final String locale = translates.getKey(), value = translates.getValue();
                    if (locale.equals(DEFAULT_LOCALE) && document.ignoreDefaultLocale) {
                        continue;
                    }
                    ProcesserState state = writers.get(locale);
                    XMLStreamWriter writer;
                    if (state == null) {
                        state = createWriter(outputDir, locale, document.filenamePattern);
                        writer = state.writer;
                        writers.put(locale, state);
                    } else {
                        writer = state.writer;
                    }
                    writer.writeStartElement("string");
                    writer.writeAttribute("name", name);
                    writer.writeCharacters(value);
                    writer.writeEndElement();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Entry<String, ProcesserState> entry : writers.entrySet()) {
            ProcesserState state = entry.getValue();
            XMLStreamWriter writer = state.writer;
            try {
                writer.writeEndElement();
                writer.writeEndDocument();
                writer.flush();
                writer.close();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                transformer.transform(
                        new StreamSource(new StringReader(state.stringWriter.toString())),
                        new StreamResult(state.os));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
