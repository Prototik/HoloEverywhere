
package org.holoeverywhere.resbuilder.type.strings;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class Grabber {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

    public static Grabber grabber(File resFolder) {
        return new Grabber(resFolder);
    }

    private final Map<String, Map<String, String>> data = new TreeMap<String, Map<String, String>>(
            TypeStrings.COMPARATOR);
    private final Map<String, File> fileMap = new HashMap<String, File>();
    private final File resFolder;
    public final Map<File, Map<String, String>> translateCache = new HashMap<File, Map<String, String>>();

    private Grabber(File resFolder) {
        this.resFolder = resFolder;
        fillFileMap();
    }

    private void fillFileMap() {
        for (File file : resFolder.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }
            String locale = null, dirName = file.getName();
            if (dirName.contentEquals("values")) {
                locale = "en";
            } else if (dirName.startsWith("values-")) {
                // en-rUS -> en_US
                locale = dirName.substring(7).replace("-r", "_");
                if (locale.length() == 5) {
                    if (locale.charAt(2) != '_') {
                        locale = null; // != en_US
                    }
                } else if (locale.length() != 2) {
                    locale = null; // != en
                }
            }
            file = new File(file, "strings.xml");
            if (locale != null && file.exists()) {
                fileMap.put(locale, file);
            }
        }
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void grab(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        for (Entry<String, File> entry : fileMap.entrySet()) {
            final String locale = entry.getKey(), translate = grab(name, entry.getValue());
            if (translate != null) {
                Map<String, String> map = data.get(locale);
                if (map == null) {
                    map = new TreeMap<String, String>(TypeStrings.COMPARATOR);
                    data.put(locale, map);
                }
                map.put(name, translate);
            }
        }
    }

    public void grab(String... names) {
        for (String name : names) {
            grab(name);
        }
    }

    protected String grab(String name, File file) {
        Map<String, String> cache = translateCache.get(file);
        if (cache == null) {
            cache = parse(file);
            translateCache.put(file, cache);
        }
        return cache.get(name);
    }

    private Map<String, String> parse(File file) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(
                    new FileInputStream(file),
                    "utf-8");
            String name = null;
            StringBuilder builder = null;
            int type, depth = 0;
            while (reader.hasNext()) {
                type = reader.next();
                switch (type) {
                    case XMLStreamConstants.START_ELEMENT:
                        if (builder != null) {
                            depth++;
                        }
                        if (!"string".equals(reader.getLocalName())) {
                            continue;
                        }
                        name = reader.getAttributeValue(null, "name");
                        if (name == null || name.length() == 0) {
                            name = null;
                            continue;
                        }
                        builder = new StringBuilder();
                        depth = 0;
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (builder != null) {
                            builder.append(reader.getText());
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (builder != null && depth-- == 0) {
                            map.put(name, builder.toString());
                            name = null;
                            builder = null;
                        }
                        break;
                }
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void reset() {
        data.clear();
        translateCache.clear();
    }
}
