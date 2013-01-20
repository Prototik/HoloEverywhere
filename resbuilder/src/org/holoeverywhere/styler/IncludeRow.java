
package org.holoeverywhere.styler;

import java.util.Map;

import org.json.JSONObject;

public class IncludeRow implements Parseable<JSONObject, IncludeRow> {
    public static enum IncludeType {
        ALL("all"), ONLY_BLOCKS("only blocks");

        public static IncludeType find(String tag) {
            for (IncludeType i : values()) {
                if (i.tag.equals(tag)) {
                    return i;
                }
            }
            return null;
        }

        public final String tag;

        private IncludeType() {
            this(null);
        }

        private IncludeType(String tag) {
            if (tag == null) {
                this.tag = name().toLowerCase().replace('_', ' ');
            } else {
                this.tag = tag;
            }
        }
    }

    public String name = "";
    public IncludeType type = IncludeType.ALL;

    @Override
    public IncludeRow parse(JSONObject data) {
        name = data.optString("name", "");
        type = IncludeType.find(data.optString("type", IncludeType.ALL.tag));
        return this;
    }

    public void process(Map<String, Block> blocks, Map<String, Block> data) {
        if (blocks == null || data == null) {
            return;
        }
        Document document = Parser.parse(name);
        if (document == null) {
            return;
        }
        switch (type) {
            default:
            case ALL:
                data.putAll(document.data);
            case ONLY_BLOCKS:
                blocks.putAll(document.blocks);
        }
        for (IncludeRow i : document.include) {
            i.process(blocks, data);
        }
    }
}
