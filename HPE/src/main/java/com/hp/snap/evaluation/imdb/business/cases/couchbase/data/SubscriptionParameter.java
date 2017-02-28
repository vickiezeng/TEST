/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class SubscriptionParameter extends PrimaryKey {
	@Field
	private String subscriptionId;// NUMBER(11)
    
	@Field
	private String deviceId;// NUMBER(11)
    
	@Field
	private String key;// VARCHAR2(254)
    
	@Field
	private String value;// VARCHAR2(4000)
    
	@Field
	private int partition;// NUMBER(4)
    
	@Field
	private String type;// VARCHAR2(255)
    
	@Field
	private long lastSyncedTime;// NUMBER(14)
	
	@Field
    private String subscriberId;//NUMBER(11)

	public SubscriptionParameter() {}
	
	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getLastSyncedTime() {
		return lastSyncedTime;
	}

	public void setLastSyncedTime(long lastSyncedTime) {
		this.lastSyncedTime = lastSyncedTime;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	@Override
	public String toString() {
		return "ClusterJ$SubscriptionParameter [id=" + id + ", subscriptionId="
				+ subscriptionId + ", deviceId=" + deviceId + ", key=" + key
				+ ", value=" + value + ", partition=" + partition + ", type="
				+ type + ", lastSyncedTime=" + lastSyncedTime
				+ ", subscriberId=" + subscriberId + "]";
	}



}
