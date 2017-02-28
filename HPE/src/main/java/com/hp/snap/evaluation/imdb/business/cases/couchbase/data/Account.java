/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class Account extends PrimaryKey {
	@Field
	private String subscriberId;
	@Field
    private long balance;
	@Field
	private byte accountType;
	@Field
	private long credit;
	@Field
	private long totalReservation;
	@Field
	private String currencyCode;
	@Field
	private byte status;
	@Field
	private int tax;
	@Field
	private long effectiveDate;
	@Field
	private long expirationDate;
	@Field
	private int partition;
	@Field
	private long lastSyncedTime;
	
	public Account() {}

	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public byte getAccountType() {
		return accountType;
	}
	public void setAccountType(byte accountType) {
		this.accountType = accountType;
	}
	public long getCredit() {
		return credit;
	}
	public void setCredit(long credit) {
		this.credit = credit;
	}
	public long getTotalReservation() {
		return totalReservation;
	}
	public void setTotalReservation(long totalReservation) {
		this.totalReservation = totalReservation;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
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
}
