/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class Contact extends PrimaryKey {

	@Field
	private byte contactType;// NUMBER(1)

	@Field
	private String contactName;// VARCHAR2(120)

	@Field
	private String contactDetail;// VARCHAR2(120)

	@Field
	private long effectiveDate;// NUMBER(14)
    
	@Field
	private long expirationDate;// NUMBER(14)
    
	@Field
	private int partition;// NUMBER(4)
    
	@Field
	private long lastSyncedDate;// NUMBER(14)
    
	@Field
	private String subscriberId;// NUMBER(11)

	public Contact() {}
	
	public byte getContactType() {
		return contactType;
	}

	public void setContactType(byte contactType) {
		this.contactType = contactType;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactDetail() {
		return contactDetail;
	}

	public void setContactDetail(String contactDetail) {
		this.contactDetail = contactDetail;
	}

	public long getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(long effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public long getLastSyncedDate() {
		return lastSyncedDate;
	}

	public void setLastSyncedDate(long lastSyncedDate) {
		this.lastSyncedDate = lastSyncedDate;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
    
}
