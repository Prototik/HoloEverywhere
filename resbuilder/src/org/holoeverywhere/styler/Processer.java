
package org.holoeverywhere.styler;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Processer {
    public static void process(Document document) {
        process(document, System.out);
    }

    public static void process(Document document, OutputStream os) {
        if (document == null || os == null) {
            return;
        }
        try {
            StringWriter writer = new StringWriter();
            process(document, XMLOutputFactory.newFactory().createXMLStreamWriter(writer));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.transform(new StreamSource(new StringReader(writer.toString())),
                    new StreamResult(os));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void process(Document document, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument("utf-8", "1.0");
        writer.writeStartElement("resources");
        document.process(writer);
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }
}
