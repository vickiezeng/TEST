package com.hp.snap.evaluation.imdb.business.cases.couchbase;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.PrimaryKey;

public abstract class AsyncResponse<T> implements Runnable {

	public PrimaryKey future;
	
	public void setFuture(PrimaryKey future) {
		this.future = future;
	}

}
