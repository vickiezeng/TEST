/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionQuota;

/**
 * @author Yang, Lin
 */
public class SubscriptionQuotaDAO extends AbstractDAO<SubscriptionQuota> {
	
	public SubscriptionQuotaDAO(Bucket bucket) {
		super(bucket);
	}
}
