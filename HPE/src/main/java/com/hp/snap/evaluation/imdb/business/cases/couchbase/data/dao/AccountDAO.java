/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Account;

/**
 * @author Yang, Lin
 */
public class AccountDAO extends AbstractDAO<Account> {
	
	public AccountDAO(Bucket bucket){
		super(bucket);
	}
    
}
