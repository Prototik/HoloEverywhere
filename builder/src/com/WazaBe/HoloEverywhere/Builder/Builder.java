package com.WazaBe.HoloEverywhere.Builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class Builder {
	public static void main(String[] args) throws IOException {
		File source = searchFile(args.length >= 1 ? args[0] : null,
				"./source.xml");
		if (source == null) {
			System.out.println("Usage: java -jar builder.jar <source.xml>");
			return;
		}
		Reader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(source), "utf-8"), 8192);
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[8192];
		int read;
		while ((read = reader.read(buffer)) > 0) {
			builder.append(buffer, 0, read);
		}
		reader.close();
		String xml = builder.toString();
		xml = Processer.process(xml);
		File output = new File(args.length >= 2 ? args[1] : "./output.xml");
		OutputStream os = new FileOutputStream(output);
		os.write(xml.getBytes("utf-8"));
		os.flush();
		os.close();
	}

	private static File searchFile(String... paths) {
		for (String path : paths) {
			if (path == null || path.length() == 0) {
				continue;
			}
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		return null;
	}
}
