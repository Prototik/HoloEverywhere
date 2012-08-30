package com.WazaBe.HoloEverywhere.Builder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processer {
	private static final Pattern DEFINE = Pattern.compile(
			"<define\\sname=\"([a-zA-Z]+)\">(.*?)</define>", Pattern.MULTILINE
					| Pattern.DOTALL);
	private static final Pattern INCLUDE = Pattern
			.compile("<include\\sname=\"([a-zA-Z]+)\"\\s/>");
	private final String xml;

	public Processer(String xml) {
		this.xml = xml;
	}

	public static String process(String xml) {
		Processer processer = new Processer(xml);
		Matcher matcher = DEFINE.matcher(xml);
		while (matcher.find()) {
			processer.defineBlock(matcher.group(1), matcher.group(2));
		}
		return processer.toString();
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
		data = xml;
		data = DEFINE.matcher(data).replaceAll("");
		Matcher matcher = INCLUDE.matcher(data);
		while (matcher.find()) {
			String replacement = blockMap.get(matcher.group(1));
			data = data.replace(matcher.group(), replacement);
		}
		data = data.replaceAll("\\s{1,}", " ").replace("> <", "><");
		data = XMLFormatter.format(data);
	}
}
