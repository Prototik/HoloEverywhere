
package org.holoeverywhere.resbuilder.type.attrs;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONArray;
import org.json.JSONObject;

public class AttrDefine {

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof AttrDefine)) {
            return false;
        }
        AttrDefine o = (AttrDefine) obj;
        if (!mName.equals(o.mName))
            return false;
        if (!mType.equals(o.mType))
            return false;
        if (!mEnumDefines.equals(o.mEnumDefines))
            return false;
        return true;
    }

    public static final Comparator<AttrDefine> COMPARATOR = new Comparator<AttrDefine>() {
        @Override
        public int compare(AttrDefine o1, AttrDefine o2) {
            return o1 == null || o1.mName == null ? (o2 == null || o2.mName == null ? 0 : -1)
                    : o1.mName.compareTo(o2.mName);
        }
    };

    private static final Comparator<EnumFlagDefine> ENUM_FLAG_COMPARATOR = new Comparator<EnumFlagDefine>() {
        @Override
        public int compare(EnumFlagDefine o1, EnumFlagDefine o2) {
            return o1 == null || o1.mName == null ? (o2 == null || o2.mName == null ? 0 : -1)
                    : o1.mName.compareTo(o2.mName);
        }
    };

    private static final class EnumFlagDefine {
        public String mName;
        public String mValue;

        public EnumFlagDefine parse(Object o) {
            if (o instanceof JSONObject) {
                JSONObject json = (JSONObject) o;
                mName = json.optString("name", null);
                mValue = json.optString("value", null);
            } else {
                mName = String.valueOf(o).trim();
                int c = mName.indexOf(' ');
                if (c > 0) {
                    // Name: name value
                    mValue = mName.substring(c + 1).trim();
                    mName = mName.substring(0, c).trim();
                }
            }
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (!(obj instanceof EnumFlagDefine)) {
                return false;
            }
            EnumFlagDefine o = (EnumFlagDefine) obj;
            if (!mName.equals(o.mName))
                return false;
            if (!mValue.equals(o.mValue))
                return false;
            return true;
        }
    }

    public String mName;
    public String mType;
    public final Set<EnumFlagDefine> mEnumDefines = new TreeSet<EnumFlagDefine>(
            ENUM_FLAG_COMPARATOR);

    public AttrDefine parse(Object o) {
        mName = null;
        mType = null;
        mEnumDefines.clear();
        if (o instanceof JSONObject) {
            JSONObject json = (JSONObject) o;
            mName = json.optString("name", null);
            mType = json.optString("type", null);
            JSONArray enumDefines = json.optJSONArray("enum");
            for (int i = 0; enumDefines != null && i < enumDefines.length(); i++) {
                mEnumDefines.add(new EnumFlagDefine().parse(enumDefines.opt(i)));
            }
        } else {
            mName = String.valueOf(o).trim();
            int c = mName.indexOf('/');
            if (c > 0) {
                // Name: name/enum1 value/enum2 value/enum3 value
                String[] enumStrings = mName.substring(c + 1).split("/");
                mName = mName.substring(0, c).trim();
                for (String enumString : enumStrings) {
                    enumString = enumString.trim();
                    if (enumString.length() > 0) {
                        mEnumDefines.add(new EnumFlagDefine().parse(enumString));
                    }
                }
            }
            c = mName.indexOf('$');
            if (c > 0) {
                // Name: name$type
                mType = mName.substring(c + 1).trim();
                mName = mName.substring(0, c).trim();
            }
        }
        if(mType == null && mEnumDefines.size() > 0) {
            mType = "enum";
        }
        return this;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("attr");
        writer.writeAttribute("name", mName);
        boolean flag = "flag".equals(mType);
        if (!flag && mType != null) {
            writer.writeAttribute("format", mType);
        }
        for (EnumFlagDefine enumDefine : mEnumDefines) {
            writer.writeStartElement(flag ? "flag" : "enum");
            writer.writeAttribute("name", enumDefine.mName);
            writer.writeAttribute("value", enumDefine.mValue);
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }
}
