/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriberCounter;

/**
 * @author Yang, Lin
 */
public class SubscriberCounterDAO extends AbstractDAO_vickie<SubscriberCounter> {
	public SubscriberCounterDAO(Bucket bucket){
		super(bucket);
	}
    
}
