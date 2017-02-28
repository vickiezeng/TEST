/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class DeviceIdentifier extends PrimaryKey {
	@Field
	private byte identifierType;
	@Field
	private String identifier;
	@Field
	private String subscriberId;
	@Field
	private int partition;
	@Field
	private long lastSyncedTime;

	public DeviceIdentifier() {}
	
	public byte getIdentifierType() {
		return identifierType;
	}
	public void setIdentifierType(byte identifierType) {
		this.identifierType = identifierType;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
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
}
