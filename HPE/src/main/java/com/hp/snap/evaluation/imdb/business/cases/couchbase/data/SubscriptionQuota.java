/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class SubscriptionQuota extends PrimaryKey {
	@Field
	private String subscriptionId;// NUMBER(11)
	@Field
	private long quotaDefinitionId;// NUMBER(11)
	@Field
	private byte type;// NUMBER(2)
	@Field
	private long quotaBalance;// NUMBER(16)
	@Field
	private long quotaReservation;// NUMBER(11)
	@Field
	private long renewalDate;// NUMBER(14)
	@Field
	private long serviceUsageCounterId;// NUMBER(11)
	@Field
	private long usedUpdate;// NUMBER(14)
	@Field
	private long maxQuota;// NUMBER(16)
	@Field
	private int partition;// NUMBER(4)
	@Field
	private long startDate;// NUMBER(14)
	@Field
	private long historyValue;
	@Field
	private long lastCarriedOverTime;
	@Field
	private long initValue;
	@Field
	private byte carriedOverType;
	@Field
	private byte historyRemainCycles;
	@Field
	private String subscriberId;// VoltDB added

	public SubscriptionQuota() {}
	
	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getQuotaDefinitionId() {
		return quotaDefinitionId;
	}

	public void setQuotaDefinitionId(long quotaDefinitionId) {
		this.quotaDefinitionId = quotaDefinitionId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getQuotaBalance() {
		return quotaBalance;
	}

	public void setQuotaBalance(long quotaBalance) {
		this.quotaBalance = quotaBalance;
	}

	public long getQuotaReservation() {
		return quotaReservation;
	}

	public void setQuotaReservation(long quotaReservation) {
		this.quotaReservation = quotaReservation;
	}

	public long getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(long renewalDate) {
		this.renewalDate = renewalDate;
	}

	public long getServiceUsageCounterId() {
		return serviceUsageCounterId;
	}

	public void setServiceUsageCounterId(long serviceUsageCounterId) {
		this.serviceUsageCounterId = serviceUsageCounterId;
	}

	public long getUsedUpdate() {
		return usedUpdate;
	}

	public void setUsedUpdate(long usedUpdate) {
		this.usedUpdate = usedUpdate;
	}

	public long getMaxQuota() {
		return maxQuota;
	}

	public void setMaxQuota(long maxQuota) {
		this.maxQuota = maxQuota;
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

	public long getHistoryValue() {
		return historyValue;
	}

	public void setHistoryValue(long historyValue) {
		this.historyValue = historyValue;
	}

	public long getLastCarriedOverTime() {
		return lastCarriedOverTime;
	}

	public void setLastCarriedOverTime(long lastCarriedOverTime) {
		this.lastCarriedOverTime = lastCarriedOverTime;
	}

	public long getInitValue() {
		return initValue;
	}

	public void setInitValue(long initValue) {
		this.initValue = initValue;
	}

	public byte getCarriedOverType() {
		return carriedOverType;
	}

	public void setCarriedOverType(byte carriedOverType) {
		this.carriedOverType = carriedOverType;
	}

	public byte getHistoryRemainCycles() {
		return historyRemainCycles;
	}

	public void setHistoryRemainCycles(byte historyRemainCycles) {
		this.historyRemainCycles = historyRemainCycles;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	@Override
	public String toString() {
		return "ClusterJ$SubscriptionQuota [id=" + id + ", subscriptionId="
				+ subscriptionId + ", quotaDefinitionId=" + quotaDefinitionId
				+ ", type=" + type + ", quotaBalance=" + quotaBalance
				+ ", quotaReservation=" + quotaReservation + ", renewalDate="
				+ renewalDate + ", serviceUsageCounterId="
				+ serviceUsageCounterId + ", usedUpdate=" + usedUpdate
				+ ", maxQuota=" + maxQuota + ", partition=" + partition
				+ ", startDate=" + startDate + ", historyValue=" + historyValue
				+ ", lastCarriedOverTime=" + lastCarriedOverTime
				+ ", initValue=" + initValue + ", carriedOverType="
				+ carriedOverType + ", historyRemainCycles="
				+ historyRemainCycles + ", subscriberId=" + subscriberId + "]";
	}


}
