
package org.holoeverywhere.resbuilder.type.strings;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamWriter;

import org.holoeverywhere.resbuilder.BuildMojo;
import org.holoeverywhere.resbuilder.FileProcesser.FileProcesserException;
import org.holoeverywhere.resbuilder.FileProcesser.ProcessResult;
import org.holoeverywhere.resbuilder.TypeProcesser;
import org.holoeverywhere.resbuilder.TypeProcesser.Type;
import org.json.JSONArray;
import org.json.JSONObject;

@Type("strings")
public class TypeStrings extends TypeProcesser {
    public static class StringsProcessResult extends ProcessResult {
        private static final long serialVersionUID = 8075005070516680457L;
        public final Map<String, Map<String, String>> data = new TreeMap<String, Map<String, String>>(
                COMPARATOR);
        public String filepattern, namePrefix;
        public boolean ignoreDefaultLocale;

        @Override
        public void flush(BuildMojo mojo) throws FileProcesserException {
            try {
                for (String locale : data.keySet()) {
                    if (ignoreDefaultLocale && DEFAULT_LOCALE.equals(locale)) {
                        continue;
                    }
                    Map<String, String> subdata = data.get(locale);
                    XMLStreamWriter writer = openWriter(mojo, null,
                            makeFileNameForLocale(locale, filepattern));
                    for (Entry<String, String> entry : subdata.entrySet()) {
                        writer.writeStartElement("string");
                        writer.writeAttribute("name", (namePrefix == null ? "" : namePrefix)
                                + entry.getKey());
                        writer.writeCharacters(entry.getValue());
                        writer.writeEndElement();
                    }
                }
            } catch (Exception e) {
                throw new FileProcesserException(e);
            }
        }
    }

    public static final Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    private static final String DEFAULT_LOCALE = "en";

    private static String[] jsonArrayToStringArray(JSONArray array) {
        final int count = array.length();
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = array.optString(i);
        }
        return result;
    }

    public static String makeFileNameForLocale(String locale, String filepattern) {
        String name = "values";
        if (!DEFAULT_LOCALE.equals(locale)) {
            name += "-" + locale.replace("_", "-r"); // en_US -> en-rUS
        }
        name += "/" + String.format(filepattern, "strings", locale);
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StringsProcessResult process(BuildMojo mojo, JSONObject json) {
        StringsProcessResult result = new StringsProcessResult();
        result.namePrefix = json.optString("prefix", "");
        result.filepattern = json.optString("filename_pattern", "%1$s.xml");
        result.ignoreDefaultLocale = json.optBoolean("ignore_default_locale");
        if (json.has("grab")) {
            Grabber grabber = Grabber.grabber(new File(mojo.androidSdkPath, "platforms/android-"
                    + mojo.androidSdkVersion + "/data/res"));
            grabber.grab(jsonArrayToStringArray(json.optJSONArray("grab")));
            result.data.putAll(grabber.getData());
        }
        if (json.has("data")) {
            JSONObject data = json.optJSONObject("data");
            Iterator<String> names = data.sortedKeys();
            while (names.hasNext()) {
                String name = names.next();
                JSONObject dataForName = data.optJSONObject(name);
                Iterator<String> locales = dataForName.sortedKeys();
                while (locales.hasNext()) {
                    String locale = locales.next();
                    Map<String, String> resultDataForLocale = result.data.get(locale);
                    if (resultDataForLocale == null) {
                        resultDataForLocale = new TreeMap<String, String>(COMPARATOR);
                        result.data.put(locale, resultDataForLocale);
                    }
                    resultDataForLocale.put(name, dataForName.optString(locale));
                }
            }
        }
        return result;
    }
}
