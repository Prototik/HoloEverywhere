
package org.holoeverywhere.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONArray;
import org.json.JSONObject;

public class Block implements Parseable<JSONObject, Block> {
    public Map<String, String> data = new HashMap<String, String>();
    public List<String> include = new ArrayList<String>();
    public String parent;

    @Override
    @SuppressWarnings("unchecked")
    public Block parse(JSONObject data) {
        parent = null;
        include.clear();
        this.data.clear();
        if (data.has("include")) {
            JSONArray a = data.optJSONArray("include");
            for (int i = 0; i < a.length(); i++) {
                include.add(String.valueOf(a.opt(i)));
            }
        }
        if (data.has("parent")) {
            parent = data.optString("parent");
        }
        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if ("include".equals(key) || "parent".equals(key)) {
                continue;
            }
            String value = String.valueOf(data.opt(key));
            if (value != null) {
                this.data.put(key, value);
            }
        }
        return this;
    }

    public void process(XMLStreamWriter writer, Map<String, Block> blocks)
            throws XMLStreamException {
        if (parent != null) {
            writer.writeAttribute("parent", parent);
        }
        for (String i : include) {
            if (blocks.containsKey(i)) {
                blocks.get(i).process(writer, blocks);
            } else {
                writer.writeComment(" Block not found: " + i + " ");
            }
        }
        for (Entry<String, String> entry : data.entrySet()) {
            writer.writeStartElement("item");
            writer.writeAttribute("name", entry.getKey());
            writer.writeCharacters(entry.getValue());
            writer.writeEndElement();
        }
    }
}
