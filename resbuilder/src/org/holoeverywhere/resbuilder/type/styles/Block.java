
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
    public Map<String, String> mData = new TreeMap<String, String>(TypeStrings.COMPARATOR);
    public List<String> mInclude = new ArrayList<String>();
    public String mParent;
    private String mName;

    @SuppressWarnings("unchecked")
    public Block parse(JSONObject data, String name) {
        mName = name;
        mParent = null;
        mInclude.clear();
        mData.clear();
        int c = mName.indexOf('<');
        if (c > 0) {
            // Name: name < parent
            mParent = mName.substring(c + 1).trim();
            mName = mName.substring(0, c).trim();
        }
        c = mName.indexOf('|');
        if (c > 0) {
            // Name: name | include1 include2
            String[] includes = mName.substring(c + 1).split(" ");
            mName = mName.substring(0, c).trim();
            for (String include : includes) {
                include = include.trim();
                if (include.length() > 0) {
                    mInclude.add(include);
                }
            }
        }
        if (data.has("include")) {
            JSONArray a = data.optJSONArray("include");
            for (int i = 0; i < a.length(); i++) {
                mInclude.add(String.valueOf(a.opt(i)));
            }
        }
        if (data.has("parent")) {
            mParent = data.optString("parent");
        }
        Iterator<String> keys = data.sortedKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            if ("include".equals(key) || "parent".equals(key)) {
                continue;
            }
            String value = String.valueOf(data.opt(key));
            if (value != null) {
                this.mData.put(key, value);
            }
        }
        return this;
    }

    public void process(XMLStreamWriter writer, Map<String, Block> blocks)
            throws XMLStreamException {
        if (mParent != null) {
            writer.writeAttribute("parent", mParent);
        }
        for (String i : mInclude) {
            if (blocks.containsKey(i)) {
                writer.writeComment("Include block: " + i);
                blocks.get(i).process(writer, blocks);
                writer.writeComment("End of block " + i);
            } else {
                writer.writeComment(" Block not found: " + i + " ");
            }
        }
        SortedMap<String, String> data = new TreeMap<String, String>(TypeStrings.COMPARATOR);
        data.putAll(this.mData);
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

    public String getName() {
        return mName;
    }
}
