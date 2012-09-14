package com.WazaBe.HoloEverywhere.Builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processer {
	private static final Pattern DEFINE = Pattern.compile(
			"<define\\sname=\"([a-zA-Z]+)\">(.*?)</define>", Pattern.MULTILINE
					| Pattern.DOTALL);
	private static final Pattern DEFINE_FILE = Pattern.compile(
			"<define\\sfile=\"([a-zA-Z\\.]+)\"\\s/>", Pattern.MULTILINE
					| Pattern.DOTALL);
	private static final Pattern INCLUDE = Pattern
			.compile("<include\\sname=\"([a-zA-Z]+)\"\\s/>");
	private final String xml;

	public Processer(String xml) {
		this.xml = xml;
	}

	public static String process(String xml) {
		Processer processer = new Processer(xml);
		Matcher matcher = DEFINE_FILE.matcher(xml);
		while (matcher.find()) {
			Matcher matcher2 = DEFINE.matcher(readFile(matcher.group(1)));
			while (matcher2.find()) {
				processer.defineBlock(matcher2.group(1), matcher2.group(2));
			}
		}
		matcher = DEFINE.matcher(xml);
		while (matcher.find()) {
			processer.defineBlock(matcher.group(1), matcher.group(2));
		}
		return processer.toString();
	}

	private static String readFile(String fileName) {
		try {
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);
			Reader reader = new BufferedReader(new InputStreamReader(is,
					"utf-8"), 8192);
			char[] buffer = new char[8192];
			int read;
			StringBuilder builder = new StringBuilder();
			while ((read = reader.read(buffer)) > 0) {
				builder.append(buffer, 0, read);
			}
			reader.close();
			return builder.toString();
		} catch (Exception e) {
			return "";
		}
	}

	private final Map<String, String> blockMap = new HashMap<String, String>();

	private void defineBlock(String name, String value) {
		blockMap.put(name, value);
		invalidate();
	}

	private boolean valid = false;

	private void invalidate() {
		valid = false;
	}

	@Override
	public synchronized String toString() {
		if (!valid) {
			rebuild();
			valid = true;
		}
		return data;
	}

	private String data;

	private synchronized void rebuild() {
		data = new String(xml);
		data = DEFINE.matcher(data).replaceAll("");
		data = DEFINE_FILE.matcher(data).replaceAll("");
		for (int i = 0; i < 3; i++) {
			Matcher matcher = INCLUDE.matcher(data);
			while (matcher.find()) {
				String replacement = blockMap.get(matcher.group(1));
				data = data.replace(matcher.group(), replacement);
			}
		}
		data = data.replaceAll("\\s{1,}", " ").replace("> <", "><");
		data = XMLFormatter.format(data);
	}
}
