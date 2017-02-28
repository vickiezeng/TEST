/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscription;

/**
 * @author Yang, Lin
 */
public class SubscriptionDAO extends AbstractDAO_vickie<Subscription> {
	public SubscriptionDAO(Bucket bucket){
		super(bucket);
	}

}
