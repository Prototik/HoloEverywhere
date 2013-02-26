
package org.holoeverywhere.resbuilder.type.attrs;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamWriter;

import org.holoeverywhere.resbuilder.BuildMojo;
import org.holoeverywhere.resbuilder.FileProcesser.FileProcesserException;
import org.holoeverywhere.resbuilder.FileProcesser.ProcessResult;
import org.holoeverywhere.resbuilder.TypeProcesser;
import org.holoeverywhere.resbuilder.TypeProcesser.Type;
import org.json.JSONArray;
import org.json.JSONObject;

@Type("attrs")
public class TypeAttrs extends TypeProcesser {
    public static class AttrsProcessResult extends ProcessResult {
        private static final long serialVersionUID = -3503835014971969667L;
        private final Set<AttrDefine> mDefineNodes = new TreeSet<AttrDefine>(
                AttrDefine.COMPARATOR);
        private final Set<StyleableDefine> mStyleableNodes = new TreeSet<StyleableDefine>(
                StyleableDefine.COMPARATOR);
        private String mOutput;

        @Override
        public void flush(BuildMojo mojo) throws FileProcesserException {
            if (mOutput == null) {
                mojo.getLog().info(" # Output property not set, skip");
                return;
            }
            XMLStreamWriter writer = openWriter(mojo, null, mOutput);
            try {
                Set<StyleableDefine> styleable = new TreeSet<StyleableDefine>(
                        StyleableDefine.COMPARATOR);
                styleable.addAll(mStyleableNodes);

                Set<AttrDefine> fullAttrDefine = new TreeSet<AttrDefine>(AttrDefine.COMPARATOR);
                fullAttrDefine.addAll(mDefineNodes);

                for (StyleableDefine define : styleable) {
                    Set<AttrDefine> simplifiedAttrList = new TreeSet<AttrDefine>(
                            AttrDefine.COMPARATOR);
                    for (AttrDefine a : define.mAttrDefines) {
                        if (a.mType == null) {
                            simplifiedAttrList.add(a);
                        } else {
                            AttrDefine newAttrDefine = new AttrDefine();
                            newAttrDefine.mName = a.mName;
                            simplifiedAttrList.add(newAttrDefine);
                            fullAttrDefine.add(a);
                        }
                    }
                    define.mAttrDefines.clear();
                    define.mAttrDefines.addAll(simplifiedAttrList);
                }
                writer.writeComment("Start attrs definition block");
                for (AttrDefine define : fullAttrDefine) {
                    define.write(writer);
                }
                writer.writeComment("Start styleable definition block");
                for (StyleableDefine define : styleable) {
                    define.write(writer);
                }
                writer.writeComment("End of any blocks");
            } catch (Exception e) {
                throw new FileProcesserException(e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttrsProcessResult process(BuildMojo mojo, JSONObject json) {
        AttrsProcessResult result = new AttrsProcessResult();
        result.mOutput = json.optString("output");
        JSONArray attrs = json.optJSONArray("attrs");
        for (int i = 0; attrs != null && i < attrs.length(); i++) {
            result.mDefineNodes.add(new AttrDefine().parse(attrs.opt(i)));
        }
        JSONObject styleable = json.optJSONObject("styleable");
        if (styleable != null) {
            Iterator<String> keys = styleable.sortedKeys();
            while (keys.hasNext()) {
                final String key = keys.next();
                result.mStyleableNodes.add(new StyleableDefine().parse(styleable.opt(key), key));
            }
        }
        return result;
    }
}
