/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

import com.couchbase.client.java.repository.annotation.Field;

/**
 * @author Yang, Lin
 */
public class OcsSession extends PrimaryKey {
    
	@Field 
	private long lastUpdateTime;// NUMBER (20)
    
	@Field 
	private long createTime;// NUMBER (20)
    
	@Field 
	private long timeoutPeriod;// NUMBER (20)
    
	@Field 
	private long timeoutLine;// NUMBER (20)
    
	@Field 
	private int state;// NUMBER (6)
    
	@Field
	private int partitionId;// NUMBER (6)
    
	@Field
	private int sessionState;// NUMBER (6)
    
	@Field
	private String serverIdentifier;// VARCHAR2(120)
    
	@Field
	private String nodeHost;// VARCHAR2(40)
    
	@Field
	private String nodeRealm;// VARCHAR2(40)
    
	@Field
	private String originalHost;// VARCHAR2 (40)
    
	@Field
	private String originalRealm;// VARCHAR2 (40)

	@Field
	private int lastCCRequestNumber;// NUMBER (10)
    
	@Field
	private int lastResultCode;// NUMBER (5)
    
	@Field
	private String serviceInfos;// VARBINARY (4194304)
    
	@Field
	private long multipleServicesSupported;// NUMBER (11)
    
	@Field
	private long subscriberId;// NUMBER (11)
    
	@Field
	private long deviceId;// NUMBER (11)
    
	@Field
	private int identifierType;// NUMBER (6)
    
	@Field
	private String identifier;// VARCHAR2(120)
    
	@Field
	private byte userEquipmentInfoType;// NUMBER (2)
    
	@Field
	private String userEquipmentInfoValue;// VARBINARY (254)
    
	@Field
	private String serviceParameters;// VARBINARY (4194304)
    
	@Field
	private String ratingSessions;// VARBINARY (4194304)
    
	@Field
	private String abmClientSession;// VARBINARY (4194304)
    
	@Field
	private String extended;// VARBINARY (4194304)

	public OcsSession() {}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getTimeoutPeriod() {
		return timeoutPeriod;
	}

	public void setTimeoutPeriod(long timeoutPeriod) {
		this.timeoutPeriod = timeoutPeriod;
	}

	public long getTimeoutLine() {
		return timeoutLine;
	}

	public void setTimeoutLine(long timeoutLine) {
		this.timeoutLine = timeoutLine;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(int partitionId) {
		this.partitionId = partitionId;
	}

	public int getSessionState() {
		return sessionState;
	}

	public void setSessionState(int sessionState) {
		this.sessionState = sessionState;
	}

	public String getServerIdentifier() {
		return serverIdentifier;
	}

	public void setServerIdentifier(String serverIdentifier) {
		this.serverIdentifier = serverIdentifier;
	}

	public String getNodeHost() {
		return nodeHost;
	}

	public void setNodeHost(String nodeHost) {
		this.nodeHost = nodeHost;
	}

	public String getNodeRealm() {
		return nodeRealm;
	}

	public void setNodeRealm(String nodeRealm) {
		this.nodeRealm = nodeRealm;
	}

	public String getOriginalHost() {
		return originalHost;
	}

	public void setOriginalHost(String originalHost) {
		this.originalHost = originalHost;
	}

	public String getOriginalRealm() {
		return originalRealm;
	}

	public void setOriginalRealm(String originalRealm) {
		this.originalRealm = originalRealm;
	}

	public int getLastCCRequestNumber() {
		return lastCCRequestNumber;
	}

	public void setLastCCRequestNumber(int lastCCRequestNumber) {
		this.lastCCRequestNumber = lastCCRequestNumber;
	}

	public int getLastResultCode() {
		return lastResultCode;
	}

	public void setLastResultCode(int lastResultCode) {
		this.lastResultCode = lastResultCode;
	}

	public String getServiceInfos() {
		return serviceInfos;
	}

	public void setServiceInfos(String serviceInfos) {
		this.serviceInfos = serviceInfos;
	}

	public long getMultipleServicesSupported() {
		return multipleServicesSupported;
	}

	public void setMultipleServicesSupported(long multipleServicesSupported) {
		this.multipleServicesSupported = multipleServicesSupported;
	}

	public long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public int getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(int identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public byte getUserEquipmentInfoType() {
		return userEquipmentInfoType;
	}

	public void setUserEquipmentInfoType(byte userEquipmentInfoType) {
		this.userEquipmentInfoType = userEquipmentInfoType;
	}

	public String getUserEquipmentInfoValue() {
		return userEquipmentInfoValue;
	}

	public void setUserEquipmentInfoValue(String userEquipmentInfoValue) {
		this.userEquipmentInfoValue = userEquipmentInfoValue;
	}

	public String getServiceParameters() {
		return serviceParameters;
	}

	public void setServiceParameters(String serviceParameters) {
		this.serviceParameters = serviceParameters;
	}

	public String getRatingSessions() {
		return ratingSessions;
	}

	public void setRatingSessions(String ratingSessions) {
		this.ratingSessions = ratingSessions;
	}

	public String getAbmClientSession() {
		return abmClientSession;
	}

	public void setAbmClientSession(String abmClientSession) {
		this.abmClientSession = abmClientSession;
	}

	public String getExtended() {
		return extended;
	}

	public void setExtended(String extended) {
		this.extended = extended;
	}
	 

}
