/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionParameter;

/**
 * @author Yang, Lin
 */
public class SubscriptionParameterDAO extends AbstractDAO<SubscriptionParameter> {
	public SubscriptionParameterDAO(Bucket bucket){
		super(bucket);
	}

}
