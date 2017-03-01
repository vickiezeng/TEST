/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import com.couchbase.client.java.Bucket;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Contact;

/**
 * @author Yang, Lin
 */
public class ContactDAO extends AbstractDAO_vickie<Contact> {
	public ContactDAO(Bucket bucket){
		super(bucket);
	}
	
}
