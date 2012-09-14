package com.WazaBe.HoloEverywhere.Builder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLFormatter {
	public static String format(InputSource is) {
		try {
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(is);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String format(String xml) {
		return format(new StringReader(xml));
	}

	public static String format(Reader reader) {
		return format(new InputSource(reader));
	}

	public static String format(InputStream is, String charset)
			throws UnsupportedEncodingException {
		return format(new InputStreamReader(is, charset));
	}

	public static String format(InputStream is) {
		return format(new InputStreamReader(is));
	}
}
