
package org.holoeverywhere.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.JSONObject;

public class Parser {
    public static Document parse(File file) {
        try {
            InputStream is = new FileInputStream(file);
            Reader reader = new InputStreamReader(is, "utf-8");
            reader = new BufferedReader(reader, 8192);
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int c;
            while ((c = reader.read(buffer)) > 0) {
                builder.append(buffer, 0, c);
            }
            reader.close();
            is.close();
            JSONObject json = new JSONObject(builder.toString());
            return parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    public static Document parse(JSONObject json) {
        return new Document().parse(json);
    }

    public static Document parse(String name) {
        return parse(new File(name));
    }
}
