/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.DeviceIdentifier;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.OcsSession;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscriber;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscription;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.DeviceIdentifierDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.OcsSessionDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.SubscriberDAO;
import com.hp.snap.evaluation.imdb.business.common.CallExecutor;
import com.hp.snap.evaluation.imdb.business.common.CallIF;
import com.hp.snap.evaluation.imdb.business.common.CallService;

/**
 * 
 * @author Yang, Lin
 */
public class CallImpl6J implements CallIF {
    private static Logger logger = Logger.getLogger(CallImpl6J.class.getName());
    public static final int RESULT_CODE_SUCCESS=0;
    
    private CallExecutor callExecutor;

    private static ClusterSessionPool sessionPool;

    private String sessionID;
    private String deviceIdentifier;

//    public static Map<Class<?>, DaoBase> daoFactory = new HashMap<Class<?>, DaoBase>();
    private static OcsSessionDAO ocsSessionDAO;
    private static DeviceIdentifierDAO deviceIdentifierDAO;
    private static SubscriberDAO subscriberDAO;
    
    
    public CallIF prepare(CallExecutor executor) {
        callExecutor = executor;
//        deviceIdentifier = callExecutor.getNextIdentifierInRandom();//"10006660001"; 
//        sessionID = DomainObjectUtils.generateSessionID(callExecutor.generateSessionID());
        deviceIdentifier = callExecutor.getNextIdentifierInSequence();//"1000006660001";
        sessionID = callExecutor.generateSessionIDWithIdentifier(deviceIdentifier);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call... executor:" + executor);
        }
        
        return this;
    }

    public static void initialize() {
		logger.info("initialize spr connection for CallImpl6J ......");
		sessionPool = new ClusterSessionPool();
		logger.info("initialize session connection for CallImpl6J ......");

		Properties config = CallService.getInstance().getConfig();
		String sprKeySpace = config.getProperty(CallService.PROP_CLUSTER_SPR_KEYSPACE);
		String sessionKeySpace = config.getProperty(CallService.PROP_CLUSTER_SESSION_KEYSPACE);
		subscriberDAO = new SubscriberDAO(sessionPool.getSession(sprKeySpace));
		deviceIdentifierDAO = new DeviceIdentifierDAO(sessionPool.getSession(sprKeySpace));
		
		ocsSessionDAO = new OcsSessionDAO(sessionPool.getSession(sessionKeySpace));
	}

    public static void shutdown() {
        sessionPool.shutdown();
    }
    
  
    public CallIF begin() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call begin... sessionID:" + sessionID + ", device identifier:" + deviceIdentifier);
        }
//        logger.info(Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
//                    + "] call begin... sessionID:" + sessionID + ", device identifier:" + deviceIdentifier);// TODO del
        OcsSession ocsSession = callExecutor.generateSession(sessionID, deviceIdentifier);

        /*------------------------------- Message In ---------------------------------------------*/
        /* session */
        ocsSessionDAO.insert(ocsSession);

        
		DeviceIdentifier deviceIdentifierJ = deviceIdentifierDAO.findByPrimaryKey("device::" + deviceIdentifier, DeviceIdentifier.class);
		String subscriberId = deviceIdentifierJ.getSubscriberId();
		
		Subscriber subscriber = subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class);
		
		ocsSession = this.callExecutor.generateSession(this.sessionID, this.deviceIdentifier);
        ocsSessionDAO.update(ocsSession);
		
        Subscription[] subscriptions = subscriber.getDevice().getSubscription();
        Subscription subscription = subscriptions[0];
		subscription.getSubscriptionQuota().setQuotaReservation(subscription.getSubscriptionQuota().getQuotaReservation() + 150);
		subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
		subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
		subscriberDAO.update(subscriber);
		
        return this;
    }
    
   
    public CallIF update() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call update... sessionID:" + sessionID + " device identifier:" + deviceIdentifier);
        }
        /* session */
        ocsSessionDAO.findByPrimaryKey(sessionID, OcsSession.class);
        
        DeviceIdentifier deviceIdentifierJ =  deviceIdentifierDAO.findByPrimaryKey("device::" + this.deviceIdentifier, DeviceIdentifier.class);
        String subscriberId = deviceIdentifierJ.getSubscriberId();
		
		/* subscriber */
		Subscriber subscriber = subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class);
		
        /*------------------------------- Message Out ---------------------------------------------*/
        OcsSession ocsSession = callExecutor.generateSession(sessionID, deviceIdentifier);
        ocsSessionDAO.update(ocsSession);
        
        Subscription[] subscriptions = subscriber.getDevice().getSubscription();
        Subscription subscription = subscriptions[0];
		subscription.getSubscriptionQuota().setQuotaBalance(subscription.getSubscriptionQuota().getQuotaBalance() - 120);
		subscription.getSubscriptionQuota().setQuotaReservation(subscription.getSubscriptionQuota().getQuotaReservation() - 150);
		subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
		subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
		subscriberDAO.update(subscriber);
		
        return this;
    }
    
   
    public CallIF end() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call end... sessionID:" + sessionID + " device identifier:" + deviceIdentifier);
        }
		/* device identifier */
        DeviceIdentifier deviceIdentifierJ = deviceIdentifierDAO.findByPrimaryKey("device::" + this.deviceIdentifier, DeviceIdentifier.class);
		String subscriberId = deviceIdentifierJ.getSubscriberId();
		
		/* subscriber */
		Subscriber subscriber = subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class);
		
		Subscription[] subscriptions = subscriber.getDevice().getSubscription();
        Subscription subscription = subscriptions[0];
		subscription.getSubscriptionQuota().setQuotaBalance(subscription.getSubscriptionQuota().getQuotaBalance() - 120);
		subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
		subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
		subscriberDAO.update(subscriber);
		
		/* session */
		OcsSession ocsSession = ocsSessionDAO.findByPrimaryKey(sessionID, OcsSession.class);
		ocsSessionDAO.delete(ocsSession);
        return this;
    }
    
	

    public String getSessionID() {
        return sessionID;
    }
	
	@Override
    public String toString()
    {
        return "CallAsyncImpl{" +
            "sessionID=" + sessionID +
            '}';
    }
}
