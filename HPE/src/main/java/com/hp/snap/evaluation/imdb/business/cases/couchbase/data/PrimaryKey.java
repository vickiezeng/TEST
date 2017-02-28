package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

public abstract class PrimaryKey {

	@Field
	private long cas;
	
	@Id
	protected String id;
	
	public PrimaryKey() {}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public long getCas() {
		return cas;
	}

	public void setCas(long cas) {
		this.cas = cas;
	}
}
