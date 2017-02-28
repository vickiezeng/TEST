/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class Subscriber extends PrimaryKey {
	@Field
    private String msisdn;    
	@Field
    private String name;
	@Field
    private byte region;
	@Field
    private byte subscriberType;
	@Field
    private byte status;
	@Field
    private String defaultAccountId;
	@Field
    private long parentSubscriberId;
	@Field
    private long effectiveDate;
	@Field
    private long expirationDate;
	@Field
    private int partition;
	@Field
    private long lastSyncedTime;
	@Field
    private byte grade;
    @Field
    private int billingDay;
    @Field
    private long cacheExpiredTime;
    @Field
    private Account account;
    @Field
    private Contact contact;
    @Field
    private SubscriberCounter counter;
    @Field
    private Device device;

    public Subscriber() {}
    
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public SubscriberCounter getCounter() {
		return counter;
	}

	public void setCounter(SubscriberCounter counter) {
		this.counter = counter;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getRegion() {
		return region;
	}

	public void setRegion(byte region) {
		this.region = region;
	}

	public byte getSubscriberType() {
		return subscriberType;
	}

	public void setSubscriberType(byte subscriberType) {
		this.subscriberType = subscriberType;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getDefaultAccountId() {
		return defaultAccountId;
	}

	public void setDefaultAccountId(String defaultAccountId) {
		this.defaultAccountId = defaultAccountId;
	}

	public long getParentSubscriberId() {
		return parentSubscriberId;
	}

	public void setParentSubscriberId(long parentSubscriberId) {
		this.parentSubscriberId = parentSubscriberId;
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

	public long getLastSyncedTime() {
		return lastSyncedTime;
	}

	public void setLastSyncedTime(long lastSyncedTime) {
		this.lastSyncedTime = lastSyncedTime;
	}

	public byte getGrade() {
		return grade;
	}

	public void setGrade(byte grade) {
		this.grade = grade;
	}

	public int getBillingDay() {
		return billingDay;
	}

	public void setBillingDay(int billingDay) {
		this.billingDay = billingDay;
	}

	public long getCacheExpiredTime() {
		return cacheExpiredTime;
	}

	public void setCacheExpiredTime(long cacheExpiredTime) {
		this.cacheExpiredTime = cacheExpiredTime;
	}

    
}
