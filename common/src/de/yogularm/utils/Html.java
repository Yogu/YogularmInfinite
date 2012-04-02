package de.yogularm.utils;

public class Html {
	public static String encodeHtml(String text) {
		return text
			.replace("%", "&amp;")
			.replace("\"", "&quot;")
			.replace("<", "&lt;")
			.replace(">", "&gt;");
	}
}
