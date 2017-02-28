/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class Device extends PrimaryKey {

	@Field
    private String productFamilyId;

	@Field
    private int partition;

	@Field
    private long effectiveDate;

	@Field
    private long expirationDate;
    
	@Field
    private long lastSyncedTime;

	@Field
    private long cacheExpiredTIme;

	@Field
    private Subscription[] subscription;

	public Device() {}
	
	public Subscription[] getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription[] subscription) {
		this.subscription = subscription;
	}

	public String getProductFamilyId() {
		return productFamilyId;
	}

	public void setProductFamilyId(String productFamilyId) {
		this.productFamilyId = productFamilyId;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
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

	public long getLastSyncedTime() {
		return lastSyncedTime;
	}

	public void setLastSyncedTime(long lastSyncedTime) {
		this.lastSyncedTime = lastSyncedTime;
	}

	public long getCacheExpiredTIme() {
		return cacheExpiredTIme;
	}

	public void setCacheExpiredTIme(long cacheExpiredTIme) {
		this.cacheExpiredTIme = cacheExpiredTIme;
	}


}
