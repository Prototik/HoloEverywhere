
package org.holoeverywhere.resbuilder.type.styles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamWriter;

import org.holoeverywhere.resbuilder.BuildMojo;
import org.holoeverywhere.resbuilder.FileProcesser.FileProcesserException;
import org.holoeverywhere.resbuilder.FileProcesser.ProcessResult;
import org.holoeverywhere.resbuilder.TypeProcesser;
import org.holoeverywhere.resbuilder.TypeProcesser.Type;
import org.json.JSONArray;
import org.json.JSONObject;

@Type("styles")
public class TypeStyles extends TypeProcesser {
    public static final class StylesProcessResult extends ProcessResult {
        private static final Comparator<String> COMPARATOR = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        private static final long serialVersionUID = -924473887807390792L;
        public Map<String, Block> blocks = new HashMap<String, Block>();
        public Map<String, Block> data = new HashMap<String, Block>();
        public List<IncludeRow> include = new ArrayList<IncludeRow>();
        public String output;
        private final TypeStyles processer;

        private StylesProcessResult(TypeStyles processer) {
            this.processer = processer;
        }

        @Override
        public void flush(BuildMojo mojo) throws FileProcesserException {
            if (output == null || output.length() == 0) {
                mojo.getLog().warn("Output field not set, skip this block");
                return;
            }
            Map<String, Block> blocks = new HashMap<String, Block>(this.blocks);
            Map<String, Block> dataS = new HashMap<String, Block>(data);
            for (IncludeRow i : include) {
                i.process(processer, mojo, blocks, dataS);
            }
            SortedMap<String, Block> data = new TreeMap<String, Block>(COMPARATOR);
            data.putAll(dataS);
            XMLStreamWriter writer = openWriter(mojo, null, output);
            try {
                for (Entry<String, Block> entry : data.entrySet()) {
                    writer.writeStartElement("style");
                    writer.writeAttribute("name", entry.getKey());
                    entry.getValue().process(writer, blocks);
                    writer.writeEndElement();
                }
            } catch (Exception e) {
                throw new FileProcesserException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public StylesProcessResult process(BuildMojo mojo, JSONObject json) {
        StylesProcessResult result = new StylesProcessResult(this);
        if (json.has("include")) {
            JSONArray include = json.optJSONArray("include");
            for (int i = 0; i < include.length(); i++) {
                result.include.add(new IncludeRow().parse(include.optJSONObject(i)));
            }
        }
        if (json.has("blocks")) {
            JSONObject blocks = json.optJSONObject("blocks");
            Iterator<String> keys = blocks.sortedKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                result.blocks.put(key, new Block().parse(blocks.optJSONObject(key)));
            }
        }
        if (json.has("data")) {
            JSONObject data = json.optJSONObject("data");
            Iterator<String> keys = data.sortedKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                result.data.put(key, new Block().parse(data.optJSONObject(key)));
            }
        }
        result.output = json.optString("output");
        return result;
    }
}
