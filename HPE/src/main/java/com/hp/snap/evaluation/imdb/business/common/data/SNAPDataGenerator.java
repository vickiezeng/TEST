/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common.data
** Date: 5/28/12				Time: 8:30 PM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Account;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Contact;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Device;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.DeviceIdentifier;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.OcsSession;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscriber;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriberCounter;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscription;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionCounter;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionParameter;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.SubscriptionQuota;
import com.hp.snap.evaluation.imdb.business.common.CallService;

/**
 *
 */
public class SNAPDataGenerator
{
	/**
	 * Construct a SNAP Data Generator
	 *
	 * @param deviceIdentiferSection device identifer section, e.g. "1000000000000-1000010000000"
	 */
	public SNAPDataGenerator(String deviceIdentiferSection)
	{
		StringTokenizer st = new StringTokenizer(deviceIdentiferSection, "-");
		String d1 = st.nextToken();
		String d2 = st.nextToken();
		if (d1.length() != 13) throw new IllegalArgumentException("device Identifier must be a 13 digits number!");
		if (!d1.startsWith("100")) throw new IllegalArgumentException("device Identifier must be started with 100!");
		if (d2.length() != 13) throw new IllegalArgumentException("device Identifier must be a 13 digits number!");
		if (!d2.startsWith("100")) throw new IllegalArgumentException("device Identifier must be started with 100!");
		_idBegin = Long.parseLong(d1);
		_idEnd = Long.parseLong(d2);
		_idCurrent = _idBegin;
	}

	/**
	 * Get Next Device Identifier
	 *
	 * @return
	 */
	public long getNextDeviceIdentifier()
	{
		_lockID.lock();
		try
		{
			_idCurrent++;
			if (_idCurrent > _idEnd)
				throw new IllegalArgumentException("DeviceIdentifier value " + _idCurrent + "exceeded Section:[" + _idBegin + "-" + _idEnd + "]");
			return _idCurrent;
		}
		finally
		{
			_lockID.unlock();
		}
	}

	/**
	 * @param deviceIdentifier must be 13 digits number, first number cannot be 0
	 * @return
	 */
	public SprDataHolder generateOneSubscriber(long deviceIdentifier)
	{
		String did = String.valueOf(deviceIdentifier);
		if (did.length() != 13) throw new IllegalArgumentException("device Identifier must be a 13 digits number!");
		if (!did.startsWith("100")) throw new IllegalArgumentException("device Identifier must be started with 100!");
		long id1 = getID1(deviceIdentifier);
		long id2 = 10000000000L + id1;
		SprDataHolder dh = new SprDataHolder();
		long now = System.currentTimeMillis();
		int partition = getPartition(deviceIdentifier);
		//
		String id1Str = "subscriber::" + id1;
		String id2Str = id2 + "";
		dh.subscriber = new Subscriber();
		dh.subscriber.setId(id1Str);
		dh.subscriber.setMsisdn(did);
		dh.subscriber.setName(generateRandomString(10));
		dh.subscriber.setRegion((byte) generateRandomInteger(99));
		dh.subscriber.setSubscriberType((byte) generateRandomInteger(99));
		dh.subscriber.setStatus((byte)generateRandomInteger(99));
		dh.subscriber.setGrade((byte) generateRandomInteger(99));
		dh.subscriber.setDefaultAccountId(id1Str);
		dh.subscriber.setParentSubscriberId(generateRandomLong(10000000000L));
		dh.subscriber.setEffectiveDate(now);
		dh.subscriber.setCacheExpiredTime(now);
		dh.subscriber.setExpirationDate(now);
		dh.subscriber.setLastSyncedTime(now);
		dh.subscriber.setPartition(partition);
		dh.subscriber.setBillingDay(generateRandomInteger(30));
		//
		dh.account = new Account();
		dh.account.setId(id1Str);
		dh.account.setSubscriberId(id1Str);
		dh.account.setBalance(1000000000L);
		dh.account.setAccountType((byte) generateRandomInteger(99));
		dh.account.setCredit(1000000000L);
		dh.account.setTotalReservation(1000000000L);
		dh.account.setCurrencyCode("CNY");
		dh.account.setStatus((byte) generateRandomInteger(5));
		dh.account.setTax(generateRandomInteger(999));
		dh.account.setEffectiveDate(now);
		dh.account.setExpirationDate(now);
		dh.account.setLastSyncedTime(now);
		dh.account.setPartition(partition);
		dh.subscriber.setAccount(dh.account);
		//
		dh.contact = new Contact();
		dh.contact.setId(id1Str + "");
		dh.contact.setContactType((byte)generateRandomInteger(9));
		dh.contact.setContactName(generateRandomString(10));
		dh.contact.setContactDetail(generateRandomString(50));
		dh.contact.setSubscriberId(id1Str);
		dh.contact.setEffectiveDate(now);
		dh.contact.setExpirationDate(now);
		dh.contact.setLastSyncedDate(now);
		dh.contact.setPartition(partition);
		dh.subscriber.setContact(dh.contact);
		//
		dh.device = new Device();
		dh.device.setId(id1Str);
		dh.device.setProductFamilyId(generateRandomString(20));
		dh.device.setEffectiveDate(now);
		dh.device.setExpirationDate(now);
		dh.device.setLastSyncedTime(now);
		dh.device.setPartition(partition);
		dh.device.setCacheExpiredTIme(now);
		dh.subscriber.setDevice(dh.device);
		//
		dh.subscriberCounter = new SubscriberCounter();
		dh.subscriberCounter.setId(id1Str + "");
		dh.subscriberCounter.setSubscriberId(id1Str);
		dh.subscriberCounter.setUsageCounterDefinitionId(generateRandomInteger(9999));
		dh.subscriberCounter.setRenewalDate(now);
		dh.subscriberCounter.setTotalUsage(1000000000L);
		dh.subscriberCounter.setUsageType((byte)generateRandomInteger(2));
		dh.subscriberCounter.setPartition(partition);
		dh.subscriberCounter.setStartDate(now);
		dh.subscriber.setCounter(dh.subscriberCounter);
		
		//
		dh.deviceIdentifer = new DeviceIdentifier();
		dh.deviceIdentifer.setId("device::" + did);
		dh.deviceIdentifer.setSubscriberId(id1Str);
		dh.deviceIdentifer.setIdentifierType((byte)0);
		dh.deviceIdentifer.setPartition(partition);
		dh.deviceIdentifer.setIdentifier(did);
		dh.deviceIdentifer.setLastSyncedTime(now);
		
		//Create 2 subscriptions
		dh.subscription = new Subscription[2];
		dh.device.setSubscription(dh.subscription);
		
		dh.subscriptionCounter = new SubscriptionCounter[2];
		dh.subscriptionQuota = new SubscriptionQuota[2];
		dh.subscriptionParameter = new SubscriptionParameter[2];
		//Subscription 1
		dh.subscription[0] = new Subscription();
		dh.subscription[0].setId(id1Str);
		dh.subscription[0].setStatus((byte)generateRandomEnum(0, 2, 6));
		dh.subscription[0].setProductId(generateRandomInteger(1000000000));
		dh.subscription[0].setStartDate(now);
		dh.subscription[0].setActivationDate(now);
		dh.subscription[0].setExpirationDate(now);
		dh.subscription[0].setRecurrentFlag((byte)1);
		dh.subscription[0].setRecurrentCount(generateRandomInteger(1000000));
		dh.subscription[0].setMainSubscriptionId(id1Str);
		dh.subscription[0].setPartition(partition);
		dh.subscription[0].setRelatedSubscriptionPartition(partition);
		dh.subscription[0].setSubscriptionType((byte)generateRandomInteger(10));
		dh.subscription[0].setRelatedSubscriptionId(generateRandomInteger(1000000000));
		
		//
		dh.subscriptionCounter[0] = new SubscriptionCounter();
		dh.subscriptionCounter[0].setId(id1Str);
		dh.subscriptionCounter[0].setSubscriptionId(id1Str);
		dh.subscriptionCounter[0].setUsageCounterDefinitionId(generateRandomInteger(1000000000));
		dh.subscriptionCounter[0].setRenewalDate(now);
		dh.subscriptionCounter[0].setStartDate(now);
		dh.subscriptionCounter[0].setTotalUsage(1000000000L);
		dh.subscriptionCounter[0].setUsageType((byte)generateRandomInteger(2));
		dh.subscriptionCounter[0].setPartition(partition);
		dh.subscriptionCounter[0].setSubscriberId(id1Str); // New Added
		//
		dh.subscriptionQuota[0] = new SubscriptionQuota();
		dh.subscriptionQuota[0].setId(id1Str);
		dh.subscriptionQuota[0].setSubscriptionId(id1Str);
		long randomInt = generateRandomInteger(1000000000);
		dh.subscriptionQuota[0].setQuotaDefinitionId(randomInt);
		dh.subscriptionQuota[0].setServiceUsageCounterId(randomInt);
		int randomInt1 = generateRandomInteger(2);
		dh.subscriptionQuota[0].setType((byte)randomInt1);
		dh.subscriptionQuota[0].setCarriedOverType((byte)randomInt1);
		dh.subscriptionQuota[0].setHistoryRemainCycles((byte)randomInt1);
		dh.subscriptionQuota[0].setQuotaBalance(1000000000L);
		dh.subscriptionQuota[0].setQuotaReservation(1000000000L);
		dh.subscriptionQuota[0].setMaxQuota(1000000000L);
		dh.subscriptionQuota[0].setInitValue(1000000000L);
		dh.subscriptionQuota[0].setHistoryValue(1000000000L);
		dh.subscriptionQuota[0].setRenewalDate(now);
		dh.subscriptionQuota[0].setStartDate(now);
		dh.subscriptionQuota[0].setUsedUpdate(now);
		dh.subscriptionQuota[0].setLastCarriedOverTime(now);
		dh.subscriptionQuota[0].setPartition(partition);
		dh.subscriptionQuota[0].setSubscriberId(id1Str); // New Added
		//
		dh.subscriptionParameter[0] = new SubscriptionParameter();
		dh.subscriptionParameter[0].setId(id1Str);
		dh.subscriptionParameter[0].setSubscriptionId(id1Str);
		dh.subscriptionParameter[0].setDeviceId(id1Str);
		dh.subscriptionParameter[0].setSubscriberId(id1Str);
		dh.subscriptionParameter[0].setKey(generateRandomString(20));
		dh.subscriptionParameter[0].setValue(generateRandomString(50));
		dh.subscriptionParameter[0].setPartition(partition);
		dh.subscriptionParameter[0].setType(generateRandomString(20));
		dh.subscriptionParameter[0].setLastSyncedTime(now);
		
		dh.subscription[0].setSubscriptionCounter(dh.subscriptionCounter[0]);
		dh.subscription[0].setSubscriptionParameter(dh.subscriptionParameter[0]);
		dh.subscription[0].setSubscriptionQuota(dh.subscriptionQuota[0]);
		
		
		//Subscription 2
		dh.subscription[1] = new Subscription();
		dh.subscription[1].setId(id2Str);
		dh.subscription[1].setStatus((byte)generateRandomEnum(0, 2, 6));
		dh.subscription[1].setProductId(generateRandomInteger(1000000000));
		dh.subscription[1].setStartDate(now);
		dh.subscription[1].setActivationDate(now);
		dh.subscription[1].setExpirationDate(now);
		dh.subscription[1].setRecurrentFlag((byte)1);
		dh.subscription[1].setRecurrentCount(generateRandomInteger(1000000));
		dh.subscription[1].setMainSubscriptionId(id1Str);
		dh.subscription[1].setPartition(partition);
		dh.subscription[1].setRelatedSubscriptionPartition(partition);
		dh.subscription[0].setSubscriptionType((byte)generateRandomInteger(10));
		dh.subscription[0].setRelatedSubscriptionId(generateRandomInteger(1000000000));
		//
		dh.subscriptionCounter[1] = new SubscriptionCounter();
		dh.subscriptionCounter[1].setId(id2Str);
		dh.subscriptionCounter[1].setSubscriptionId(id2Str);
		dh.subscriptionCounter[1].setUsageCounterDefinitionId(generateRandomInteger(1000000000));
		dh.subscriptionCounter[1].setRenewalDate(now);
		dh.subscriptionCounter[1].setStartDate(now);
		dh.subscriptionCounter[1].setTotalUsage(1000000000L);
		dh.subscriptionCounter[1].setUsageType((byte)generateRandomInteger(2));
		dh.subscriptionCounter[1].setPartition(partition);
		dh.subscriptionCounter[1].setSubscriberId(id1Str); // New Added
		//
		dh.subscriptionQuota[1] = new SubscriptionQuota();
		dh.subscriptionQuota[1].setId(id2Str);
		dh.subscriptionQuota[1].setSubscriptionId(id2Str);
		long randomInt3 = generateRandomInteger(1000000000);
		dh.subscriptionQuota[1].setQuotaDefinitionId(randomInt3);
		dh.subscriptionQuota[1].setServiceUsageCounterId(randomInt3);
		byte randomInt4 = (byte)generateRandomInteger(2);
		dh.subscriptionQuota[1].setType(randomInt4);
		dh.subscriptionQuota[1].setCarriedOverType(randomInt4);
		dh.subscriptionQuota[1].setHistoryRemainCycles(randomInt4);
		dh.subscriptionQuota[1].setQuotaBalance(1000000000L);
		dh.subscriptionQuota[1].setQuotaReservation(1000000000L);
		dh.subscriptionQuota[1].setMaxQuota(1000000000L);
		dh.subscriptionQuota[1].setInitValue(1000000000L);
		dh.subscriptionQuota[1].setHistoryValue(1000000000L);
		dh.subscriptionQuota[1].setRenewalDate(now);
		dh.subscriptionQuota[1].setStartDate(now);
		dh.subscriptionQuota[1].setUsedUpdate(now);
		dh.subscriptionQuota[1].setLastCarriedOverTime(now);
		dh.subscriptionQuota[1].setPartition(partition);
		dh.subscriptionQuota[1].setSubscriberId(id1Str); // New Added
		//
		dh.subscriptionParameter[1] = new SubscriptionParameter();
		dh.subscriptionParameter[1].setId(id2Str);
		dh.subscriptionParameter[1].setSubscriptionId(id2Str);
		dh.subscriptionParameter[1].setDeviceId(id1Str);
		dh.subscriptionParameter[1].setSubscriberId(id1Str);
		dh.subscriptionParameter[1].setKey(generateRandomString(20));
		dh.subscriptionParameter[1].setValue(generateRandomString(50));
		dh.subscriptionParameter[1].setPartition(partition);
		dh.subscriptionParameter[1].setType(generateRandomString(20));
		dh.subscriptionParameter[1].setLastSyncedTime(now);
		
		dh.subscription[1].setSubscriptionCounter(dh.subscriptionCounter[1]);
		dh.subscription[1].setSubscriptionParameter(dh.subscriptionParameter[1]);
		dh.subscription[1].setSubscriptionQuota(dh.subscriptionQuota[1]);

		return dh;
	}

	/**
	 * Get a session ID
	 *
	 * @return
	 */
	public String generateSessionID()
	{
		return makeSessionID("SNAP-Test-Framework(bow)");
	}

	/**
	 * Get a session ID
	 *
	 * @return
	 */
	public String generateSessionIDWithIdentifier(String identifier)
	{
		return makeSessionID(identifier);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Generate Session Data
	 *
	 * @param sessionID       Session ID
	 * @param deviceIdentifer Device Identifier
	 * @return
	 */
	public OcsSession generateSession(String sessionID, long deviceIdentifer)
	{
		OcsSession s=null;
		if(_useSessionPool)
		{
			try
			{
				_lockSessionPoll.lock();
				s = _sessionPollIdle.pollFirst();
			}
			finally
			{
				_lockSessionPoll.unlock();
			}
		}
		return genSession(sessionID,deviceIdentifer,s);
	}

	private OcsSession genSession(String sessionID, long deviceIdentifer, OcsSession s)
	{
		long now = System.currentTimeMillis();
		if (s == null)
		{
			s = new OcsSession();
			int randomInt1 = generateRandomInteger(10000000);
			s.setTimeoutLine(randomInt1);
			s.setTimeoutPeriod(randomInt1);
			int randomInt2 = generateRandomInteger(1024);
			s.setState(randomInt2);
			s.setSessionState(randomInt2);
			s.setPartitionId(getPartition(deviceIdentifer));
			String randomString1 = generateRandomString(15);
			s.setServerIdentifier(randomString1);
			String randomString2 = generateRandomString(30);
			String randomString3 = generateRandomString(30);
			s.setNodeHost(randomString2);
			s.setNodeRealm(randomString2);
			s.setOriginalHost(randomString3);
			s.setOriginalRealm(randomString3);
			s.setLastCCRequestNumber(generateRandomInteger(1024));
			s.setLastResultCode(generateRandomInteger(10000));
//			s.setServiceInfos(generateRandomBinary(256, 1024));
			s.setServiceInfos(generateRandomString(256, 1024));
			s.setMultipleServicesSupported(generateRandomInteger(1024));
			long id1 = getID1(deviceIdentifer);
			s.setSubscriberId(id1);
			s.setDeviceId(id1);
			s.setIdentifierType(0);
			s.setUserEquipmentInfoType((byte)generateRandomInteger(10));
//			s.setUserEquipmentInfoValue(generateRandomBinary(10, 50));
//			s.setServiceParameters(generateRandomBinary(10, 128));
//			s.setRatingSessions(generateRandomBinary(512, 1024));
//			s.setAbmClientSession(generateRandomBinary(64, 512));
//			s.setExtended(generateRandomBinary(0, 128));
			s.setUserEquipmentInfoValue(generateRandomString(10, 50));
			s.setServiceParameters(generateRandomString(10, 128));
			s.setRatingSessions(generateRandomString(512, 1024));
			s.setAbmClientSession(generateRandomString(64, 512));
			s.setExtended(generateRandomString(0, 128));
		}
		s.setId(sessionID);
		s.setLastUpdateTime(now);
		s.setCreateTime(now);
		s.setIdentifier(String.valueOf(deviceIdentifer));
		return s;
	}
	private LinkedList<OcsSession> _sessionPollIdle = new LinkedList<OcsSession>();

	public void returnSession(OcsSession session)
	{
		if(!_useSessionPool) return;
		try
		{
			_lockSessionPoll.lock();
			_sessionPollIdle.offer(session);
		}
		finally
		{
			_lockSessionPoll.unlock();
		}
	}

	final private transient Lock _lockSessionPoll = new ReentrantLock();

	public int getSessionPoolIdleCount()
	{
		return _sessionPollIdle.size();
	}
	private boolean _useSessionPool;

	public boolean isUseSessionPool()
	{
		return _useSessionPool;
	}

	public void setUseSessionPool(boolean useSessionPool)
	{
		_useSessionPool = useSessionPool;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	public byte[] generateRandomBinary(int lenMin, int lenMax)
	{
		int length = lenMin + getRand().nextInt(lenMax - lenMin);
		byte[] text=_mapLen2Bytes.get(length);
		if(text!=null)	return text;
		text = new byte[length];
		generateRandomBytes(text);
		_mapLen2Bytes.put(length,text);
		return text;
	}

	public String generateRandomString(int length)
	{
		String text=_mapLen2String.get(length);
		if(text!=null)	return text;

		char[] textc = new char[length];
		for (int i = 0; i < length; i++)
		{
			textc[i] = CHARS.charAt(getRand().nextInt(CHARS.length()));
		}
		text=new String(textc);
		_mapLen2String.put(length,text);
		return text;
	}

	public String generateRandomString(int lengthMin, int LengthMax)
	{
		int length = lengthMin + getRand().nextInt(LengthMax - lengthMin);
		return generateRandomString(length);
	}

	private byte[] generateRandomBytes(byte[] bytes)
	{
		getRand().nextBytes(bytes);
		return bytes;
	}

	public int generateRandomInteger(int max)
	{
		return getRand().nextInt(max);
	}

	public long generateRandomLong(long max)
	{
		return Math.abs(getRand().nextLong()) % max;
	}

	public int generateRandomEnum(int... values)
	{
		int len = values.length;
		int i = getRand().nextInt(len);
		return values[i];
	}

	private int getPartition(long deviceIdentifier)
	{
		return (int) (deviceIdentifier / 1000000 - 1000000);
	}

	private long getID1(long deviceIdentifier)
	{
		long id1 = deviceIdentifier % 10000000000L;//keep last 10 digits
		id1 = 10000000000L + id1;
		return id1;
	}


	private long _idBegin, _idEnd, _idCurrent;
	final private Lock _lockID = new ReentrantLock();

	static public class SprDataHolder
	{
		public Subscriber subscriber;
		public SubscriberCounter subscriberCounter;
		public Account account;
		public Contact contact;
		public Device device;
		public DeviceIdentifier deviceIdentifer;
		public Subscription subscription[];
		public SubscriptionCounter subscriptionCounter[];
		public SubscriptionQuota subscriptionQuota[];
		public SubscriptionParameter subscriptionParameter[];

		@Override
		public String toString()
		{
			return "SprDataHolder{" +
					"subscriber=" + subscriber + "\n" +
					", subscriberCounter=" + subscriberCounter + "\n" +
					", account=" + account + "\n" +
					", contact=" + contact + "\n" +
					", device=" + device + "\n" +
					", deviceIdentifer=" + deviceIdentifer + "\n" +
					", subscription=" + (subscription == null ? null : Arrays.asList(subscription)) + "\n" +
					", subscriptionCounter=" + (subscriptionCounter == null ? null : Arrays.asList(subscriptionCounter)) + "\n" +
					", subscriptionQuota=" + (subscriptionQuota == null ? null : Arrays.asList(subscriptionQuota)) + "\n" +
					", subscriptionParameter=" + (subscriptionParameter == null ? null : Arrays.asList(subscriptionParameter)) + "\n" +
					'}';
		}
	}

	static private String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

// TODO delete
//	public void insertSPR2JDBC(Connection conn, SprDataHolder sdh) throws SQLException
//	{
//		_daoSubscriber.insert(conn, sdh.subscriber);
//		_daoAccount.insert(conn, sdh.account);
//		_daoContact.insert(conn, sdh.contact);
//		_daoDevice.insert(conn, sdh.device);
//		_daoDeviceIdentifier.insert(conn, sdh.deviceIdentifer);
//		_daoSubscriberCounter.insert(conn, sdh.subscriberCounter);
//
//		_daoSubscription.insert(conn, sdh.subscription[0]);
//		_daoSubscriptionParameter.insert(conn, sdh.subscriptionParameter[0]);
//		_daoSubscriptionCounter.insert(conn, sdh.subscriptionCounter[0]);
//		_daoSubscriptionQuota.insert(conn, sdh.subscriptionQuota[0]);
//
//		_daoSubscription.insert(conn, sdh.subscription[1]);
//		_daoSubscriptionParameter.insert(conn, sdh.subscriptionParameter[1]);
//		_daoSubscriptionCounter.insert(conn, sdh.subscriptionCounter[1]);
//		_daoSubscriptionQuota.insert(conn, sdh.subscriptionQuota[1]);
//
//	}
//
//	public void insertSession2JDBC(Connection conn, SNAP$OcsSession session) throws SQLException
//	{
//		_daoOcsSession.insert(conn, session);
//	}
//
//	private SNAP$SubscriberDAO _daoSubscriber = new SNAP$SubscriberDAO();
//	private SNAP$AccountDAO _daoAccount = new SNAP$AccountDAO();
//	private SNAP$ContactDAO _daoContact = new SNAP$ContactDAO();
//	private SNAP$DeviceDAO _daoDevice = new SNAP$DeviceDAO();
//	private SNAP$DeviceIdentifierDAO _daoDeviceIdentifier = new SNAP$DeviceIdentifierDAO();
//	private SNAP$SubscriberCounterDAO _daoSubscriberCounter = new SNAP$SubscriberCounterDAO();
//	private SNAP$SubscriptionDAO _daoSubscription = new SNAP$SubscriptionDAO();
//	private SNAP$SubscriptionParameterDAO _daoSubscriptionParameter = new SNAP$SubscriptionParameterDAO();
//	private SNAP$SubscriptionQuotaDAO _daoSubscriptionQuota = new SNAP$SubscriptionQuotaDAO();
//	private SNAP$SubscriptionCounterDAO _daoSubscriptionCounter = new SNAP$SubscriptionCounterDAO();
//	private SNAP$OcsSessionDAO _daoOcsSession = new SNAP$OcsSessionDAO();
	private Map<Integer,byte[]> _mapLen2Bytes=new ConcurrentHashMap<Integer, byte[]>();
	private Map<Integer,String> _mapLen2String=new ConcurrentHashMap<Integer, String>();
	private ThreadLocal<Random> _threadRandom=new ThreadLocal<Random>();
	private Random getRand()
	{
		Random rand=_threadRandom.get();
		if(rand==null)
		{
			rand=new Random();
			_threadRandom.set(rand);
		}
		return rand;
	}
	static public String makeSessionID(String host)
	{
		return host + ";" + _sessionIdHigh + ";" + (_sessionIdLow.getAndIncrement());

	}
	private static final int _sessionIdHigh = (int) (System.currentTimeMillis() / 1000L);
	private static AtomicLong _sessionIdLow = new AtomicLong(0);

	public static void main(String[] args) throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@bowvm:1521:orcl", "snap_imdb", "snap_imdb");
		//
		SNAPDataGenerator app = new SNAPDataGenerator("1000000000000-1000010000000");
		//Session
		String sid = app.generateSessionID();
		long did = app.getNextDeviceIdentifier();
//		comment off on 20160923 TODO
//		SNAP$OcsSession session = app.generateSession(sid, did);
//		app.insertSession2JDBC(conn, session);
//		System.out.println(session);
		CallService.inputString(">", "[Enter] to continue...");
		//1 Subscriber
		SNAPDataGenerator.SprDataHolder sdh = app.generateOneSubscriber(did);
		System.out.println(sdh);
		//app.insertSPR2JDBC(conn, sdh); comment off on 20160923 TODO
		CallService.inputString(">", "[Enter] to continue...");
		//
		for (int i = 0; i < 3; i++)
		{
			did = app.getNextDeviceIdentifier();
			sdh = app.generateOneSubscriber(did);
			System.out.println(sdh);
			//app.insertSPR2JDBC(conn, sdh); comment off on 20160923 TODO
			CallService.inputString(">", "[Enter] to continue...");
		}
		//
		conn.close();
	}
}
