package com.hp.snap.evaluation.imdb.business.cases;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.couchbase.client.deps.com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.MapperFeature;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectReader;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectWriter;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.SerializationFeature;


/**
 * JsonConverter implementation based on the Jackson Databind library.
 * 
 * @author Tony Piazza
 */
public class CBJacksonConverter implements JsonConverter {
	private final ObjectMapper mapper = 
		new ObjectMapper()
			.setSerializationInclusion(Include.NON_NULL)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));

	private final ObjectReader reader = mapper.reader();
	private final ObjectWriter writer = mapper.writer();
	
	public <T> T fromJson(String source, Class<T> valueType) {
		try {
			return reader.forType(valueType).readValue(source);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T> String toJson(T source) {
		try {
			return writer.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	
	public <T> T fromBytes(byte[] source, Class<T> valueType) {
		try {
			return reader.forType(valueType).readValue(source);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}


	public <T> byte[] toBytes(T source) {
		try {
			return writer.writeValueAsBytes(source);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}