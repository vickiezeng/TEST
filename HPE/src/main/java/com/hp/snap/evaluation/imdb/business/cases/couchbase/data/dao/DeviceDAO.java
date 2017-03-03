/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.google.common.util.concurrent.ListenableFuture;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.AsyncResponse;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Device;

/**
 * @author Yang, Lin
 */
public class DeviceDAO extends AbstractDAO<Device> {
	public DeviceDAO(Bucket bucket){
		super(bucket);
	}

}
