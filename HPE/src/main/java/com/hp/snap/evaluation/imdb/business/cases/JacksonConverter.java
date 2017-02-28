package com.hp.snap.evaluation.imdb.business.cases;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;


public class JacksonConverter implements JsonConverter {
	private final ObjectMapper mapper = 
		new ObjectMapper()
			.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
			.setSerializationInclusion(Include.NON_NULL)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))
			.registerModule(new AfterburnerModule());
	       
//	private final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
//         {
//             mapper.registerModule(new AfterburnerModule());
//         }

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