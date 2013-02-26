
package org.holoeverywhere.resbuilder.type.attrs;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONArray;

public class StyleableDefine {
    public static final Comparator<StyleableDefine> COMPARATOR = new Comparator<StyleableDefine>() {
        @Override
        public int compare(StyleableDefine o1, StyleableDefine o2) {
            return o1 == null || o1.mName == null ? (o2 == null || o2.mName == null ? 0 : -1)
                    : o1.mName.compareTo(o2.mName);
        }
    };

    public String mName;
    public final Set<AttrDefine> mAttrDefines = new TreeSet<AttrDefine>(AttrDefine.COMPARATOR);

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof StyleableDefine)) {
            return false;
        }
        StyleableDefine o = (StyleableDefine) obj;
        if (!mName.equals(o.mName))
            return false;
        if (!mAttrDefines.equals(o.mAttrDefines))
            return false;
        return true;
    }

    public StyleableDefine parse(Object o, String name) {
        mName = name;
        mAttrDefines.clear();
        if (o instanceof JSONArray) {
            JSONArray json = (JSONArray) o;
            for (int i = 0; i < json.length(); i++) {
                mAttrDefines.add(new AttrDefine().parse(json.opt(i)));
            }
        } else {
            String[] defines = String.valueOf(o).split("/");
            for (String define : defines) {
                define = define.trim();
                if (define.length() > 0) {
                    mAttrDefines.add(new AttrDefine().parse(define));
                }
            }
        }
        return this;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("declare-styleable");
        writer.writeAttribute("name", mName);
        for (AttrDefine define : mAttrDefines) {
            define.write(writer);
        }
        writer.writeEndElement();
    }
}
