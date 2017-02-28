/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionCounter;

/**
 * @author Yang, Lin
 */
public class SubscriptionCounterDAO extends AbstractDAO_vickie<SubscriptionCounter> {
	
	public SubscriptionCounterDAO(Bucket bucket) {
		super(bucket);
	}
	
}
