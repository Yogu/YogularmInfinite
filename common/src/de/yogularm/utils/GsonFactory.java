package de.yogularm.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	public static Gson createGson() {
		return new GsonBuilder()
			//.excludeFieldsWithModifiers(Modifier.TRANSIENT | Modifier.STATIC)
			.excludeFieldsWithoutExposeAnnotation()
			.create();
	}
}
