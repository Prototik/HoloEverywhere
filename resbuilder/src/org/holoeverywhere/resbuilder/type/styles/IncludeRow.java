
package org.holoeverywhere.resbuilder.type.styles;

import java.util.Map;

import org.holoeverywhere.resbuilder.BuildMojo;
import org.holoeverywhere.resbuilder.FileProcesser.FileProcesserException;
import org.holoeverywhere.resbuilder.type.styles.TypeStyles.StylesProcessResult;
import org.json.JSONObject;

public class IncludeRow {
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

    public IncludeRow parse(JSONObject data) {
        name = data.optString("name", "");
        type = IncludeType.find(data.optString("type", IncludeType.ALL.tag));
        return this;
    }

    public void process(TypeStyles processer, BuildMojo mojo, Map<String, Block> blocks,
            Map<String, Block> data) throws FileProcesserException {
        if (blocks == null || data == null) {
            return;
        }
        StylesProcessResult result = mojo.processer.process(name).find(StylesProcessResult.class);
        if (result == null) {
            return;
        }
        switch (type) {
            default:
            case ALL:
                data.putAll(result.data);
            case ONLY_BLOCKS:
                blocks.putAll(result.blocks);
        }
        for (IncludeRow i : result.include) {
            i.process(processer, mojo, blocks, data);
        }
    }
}
