package com.hp.snap.evaluation.imdb.business.cases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JsonConverter implementation based on the GSON library.
 * 
 * @author Tony Piazza
 */
public class GsonConverter implements JsonConverter {
	private final Gson gson = 
		new GsonBuilder()
			.create();

	public <T> T fromJson(String source, Class<T> type) {
		return gson.fromJson(source, type);
	}

	public <T> String toJson(T source) {
		return gson.toJson(source);
	}

	public <T> T fromBytes(byte[] source, Class<T> type) {
		return null;
	}

	public <T> byte[] toBytes(T source) {
		// TODO Auto-generated method stub
		return null;
	}
}