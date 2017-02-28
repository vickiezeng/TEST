/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class Subscription extends PrimaryKey {
    
	@Field
	private byte status;// NUMBER(2)
    
	@Field
	private long productId;// NUMBER(11)
    
	@Field
	private long startDate;// NUMBER(14)
    
	@Field
	private long activationDate;// NUMBER(14)
    
	@Field
	private long expirationDate;// NUMBER(14)
    
	@Field
	private byte recurrentFlag;// NUMBER(1)
    
	@Field
	private int recurrentCount;// NUMBER(6)
    
	@Field
	private String mainSubscriptionId;// NUMBER(11)
    
	@Field
	private int partition;// NUMBER(4)
    
	@Field
	private long lastSyncedTime;// NUMBER(14)
    
	@Field
	private byte subscriptionType;// NUMBER(1)
    
	@Field
	private long relatedSubscriptionId;// NUMBER(11)
    
	@Field
	private int relatedSubscriptionPartition;// NUMBER(4)

	@Field
	private SubscriptionCounter subscriptionCounter;
	
	@Field
	private SubscriptionParameter subscriptionParameter;

	@Field
	private SubscriptionQuota subscriptionQuota;
	
	public Subscription() {}
	
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(long activationDate) {
		this.activationDate = activationDate;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public byte getRecurrentFlag() {
		return recurrentFlag;
	}

	public void setRecurrentFlag(byte recurrentFlag) {
		this.recurrentFlag = recurrentFlag;
	}

	public int getRecurrentCount() {
		return recurrentCount;
	}

	public void setRecurrentCount(int recurrentCount) {
		this.recurrentCount = recurrentCount;
	}

	public String getMainSubscriptionId() {
		return mainSubscriptionId;
	}

	public void setMainSubscriptionId(String mainSubscriptionId) {
		this.mainSubscriptionId = mainSubscriptionId;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public long getLastSyncedTime() {
		return lastSyncedTime;
	}

	public void setLastSyncedTime(long lastSyncedTime) {
		this.lastSyncedTime = lastSyncedTime;
	}

	public byte getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(byte subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public long getRelatedSubscriptionId() {
		return relatedSubscriptionId;
	}

	public void setRelatedSubscriptionId(long relatedSubscriptionId) {
		this.relatedSubscriptionId = relatedSubscriptionId;
	}

	public int getRelatedSubscriptionPartition() {
		return relatedSubscriptionPartition;
	}

	public void setRelatedSubscriptionPartition(int relatedSubscriptionPartition) {
		this.relatedSubscriptionPartition = relatedSubscriptionPartition;
	}

	public SubscriptionCounter getSubscriptionCounter() {
		return subscriptionCounter;
	}

	public void setSubscriptionCounter(SubscriptionCounter subscriptionCounter) {
		this.subscriptionCounter = subscriptionCounter;
	}

	public SubscriptionParameter getSubscriptionParameter() {
		return subscriptionParameter;
	}

	public void setSubscriptionParameter(SubscriptionParameter subscriptionParameter) {
		this.subscriptionParameter = subscriptionParameter;
	}

	public SubscriptionQuota getSubscriptionQuota() {
		return subscriptionQuota;
	}

	public void setSubscriptionQuota(SubscriptionQuota subscriptionQuota) {
		this.subscriptionQuota = subscriptionQuota;
	}
}
