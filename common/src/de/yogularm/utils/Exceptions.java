package de.yogularm.utils;

public class Exceptions {
	public static String formatException(Throwable e) {
		String message = e.getMessage();
		if (e.getCause() != null) {
			String inner = formatException(e.getCause());
			// Avoid showing the same message twice
			if (e.getCause().getMessage() == message)
				return inner;
			else
				return String.format("%s (%s)", message, inner);
		} else
			return message;
	}
}
