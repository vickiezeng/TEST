/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.DeviceIdentifier;

/**
 * @author Yang, Lin
 */
public class DeviceIdentifierDAO extends AbstractDAO_vickie<DeviceIdentifier> {
	public DeviceIdentifierDAO(Bucket bucket){
		super(bucket);
	}
}
