/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class SubscriptionCounter extends PrimaryKey {
	@Field
	private String subscriptionId;// NUMBER(11)
	
	@Field
	private long usageCounterDefinitionId;// NUMBER(11)
	
	@Field
	private long renewalDate;// NUMBER(14)
	
	@Field
	private long totalUsage;// NUMBER(16)
	
	@Field
	private byte usageType;// NUMBER(2)
	
	@Field
	private int partition;// NUMBER(4)
	
	@Field
	private long startDate;// NUMBER(14)
	
	@Field
    private String subscriberId;// VoltDB added

	public SubscriptionCounter() {}
	
	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getUsageCounterDefinitionId() {
		return usageCounterDefinitionId;
	}

	public void setUsageCounterDefinitionId(long usageCounterDefinitionId) {
		this.usageCounterDefinitionId = usageCounterDefinitionId;
	}

	public long getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(long renewalDate) {
		this.renewalDate = renewalDate;
	}

	public long getTotalUsage() {
		return totalUsage;
	}

	public void setTotalUsage(long totalUsage) {
		this.totalUsage = totalUsage;
	}

	public byte getUsageType() {
		return usageType;
	}

	public void setUsageType(byte usageType) {
		this.usageType = usageType;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	@Override
	public String toString() {
		return "ClusterJ$SubscriptionCounter [id=" + id + ", subscriptionId="
				+ subscriptionId + ", usageCounterDefinitionId="
				+ usageCounterDefinitionId + ", renewalDate=" + renewalDate
				+ ", totalUsage=" + totalUsage + ", usageType=" + usageType
				+ ", partition=" + partition + ", startDate=" + startDate
				+ ", subscriberId=" + subscriberId + "]";
	}

}
