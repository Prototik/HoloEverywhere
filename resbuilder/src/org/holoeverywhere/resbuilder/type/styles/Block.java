
package org.holoeverywhere.resbuilder.type.styles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.holoeverywhere.resbuilder.type.strings.TypeStrings;
import org.json.JSONArray;
import org.json.JSONObject;

public class Block {
    public Map<String, String> data = new TreeMap<String, String>(TypeStrings.COMPARATOR);
    public List<String> include = new ArrayList<String>();
    public String parent;

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
        Iterator<String> keys = data.sortedKeys();
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
                writer.writeComment("Include block: " + i);
                blocks.get(i).process(writer, blocks);
                writer.writeComment("End of block " + i);
            } else {
                writer.writeComment(" Block not found: " + i + " ");
            }
        }
        SortedMap<String, String> data = new TreeMap<String, String>(TypeStrings.COMPARATOR);
        data.putAll(this.data);
        for (Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey(), value = entry.getValue();
            writer.writeStartElement("item");
            writer.writeAttribute("name", key);
            if (value.startsWith("fraction:")) {
                writer.writeAttribute("type", "fraction");
                value = value.substring("fraction:".length());
            }
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }
}
