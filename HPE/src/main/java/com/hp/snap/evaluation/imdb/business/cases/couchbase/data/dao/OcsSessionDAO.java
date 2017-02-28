/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import java.util.Properties;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.ReplicateTo;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.OcsSession;
import com.hp.snap.evaluation.imdb.business.common.CallService;

/**
 * @author Yang, Lin
 */
public class OcsSessionDAO extends AbstractDAO_vickie<OcsSession> {
	
	public OcsSessionDAO(Bucket bucket){
		super(bucket);
		Properties config = CallService.getInstance().getConfig();
		persistTo = PersistTo.valueOf(config.getProperty("session.PersistTo"));
		replicateTo = ReplicateTo.valueOf(config.getProperty("session.ReplicateTo"));
	}
    
}
