
package org.holoeverywhere.resbuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONObject;
import org.json.XML;

public class XmlUtils {
    private static final String ATTRS = "attrs";
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newFactory();

    public static String json2xml(JSONObject json) {
        try {
            if (json.length() != 1) {
                JSONObject root = new JSONObject();
                root.put("root", json);
                json = root;
            }
            StringWriter writer = new StringWriter();
            XMLStreamWriter xml = OUTPUT_FACTORY.createXMLStreamWriter(writer);
            xml.writeStartDocument();
            json2xml(json, xml);
            xml.writeEndDocument();
            return writer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static void json2xml(JSONObject json, XMLStreamWriter xml) throws Exception {
        Iterator<String> keys;
        if (json.has(ATTRS)) {
            JSONObject attrs = json.getJSONObject(ATTRS);
            keys = attrs.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                xml.writeAttribute(key, attrs.optString(key, ""));
            }
        }
        keys = json.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (ATTRS.equals(key)) {
                continue;
            }
            xml.writeStartElement(key);
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                json2xml((JSONObject) value, xml);
            } else {
                xml.writeCharacters(String.valueOf(value));
            }
            xml.writeEndElement();
        }
    }

    public static JSONObject xml2json(InputStream is) {
        return xml2json(new InputStreamReader(is));
    }

    public static JSONObject xml2json(Reader reader) {
        try {
            char[] buffer = new char[1024];
            int c;
            StringBuilder builder = new StringBuilder();
            while ((c = reader.read(buffer)) > 0) {
                builder.append(buffer, 0, c);
            }
            reader.close();
            return xml2json(builder.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static JSONObject xml2json(String xml) {
        try {
            return XML.toJSONObject(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
