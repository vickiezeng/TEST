/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.AsyncResponse;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.DeviceIdentifier;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.OcsSession;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscriber;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscription;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.DeviceIdentifierDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.OcsSessionDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.SubscriberDAO;
import com.hp.snap.evaluation.imdb.business.common.CallAsyncIF;
import com.hp.snap.evaluation.imdb.business.common.CallExecutor;
import com.hp.snap.evaluation.imdb.business.common.CallExecutorAsync;
import com.hp.snap.evaluation.imdb.business.common.CallIF;
import com.hp.snap.evaluation.imdb.business.common.CallService;

/**
 * 
 * @author Yang, Lin
 */
public class CallImpl6JAsync extends AsyncResponse implements CallAsyncIF {
    private static final String DEVICE = "device::";
	private static Logger logger = Logger.getLogger(CallImpl6JAsync.class.getName());
    public static final int RESULT_CODE_SUCCESS=0;
    
    private CallExecutorAsync callExecutor;

    private static ClusterSessionPool sessionPool;

    private String sessionID;
    private String deviceIdentifier;

//    public static Map<Class<?>, DaoBase> daoFactory = new HashMap<Class<?>, DaoBase>();
    private static OcsSessionDAO ocsSessionDAO;
    private static DeviceIdentifierDAO deviceIdentifierDAO;
    private static SubscriberDAO subscriberDAO;
    
    private StateCall _stateCall = StateCall.idle;
    private StateDB _stateDBBegin = StateDB.idle;
    private StateDB _stateDBUpdate = StateDB.idle;
    private StateDB _stateDBEnd = StateDB.idle;
    
    
    private enum StateCall
    {
        idle,//
        begining, //In Begin
        updating, //In Update
        ending    //In end
    }

    private enum StateDB
    {
        idle,//
        U0,U1,U2,U3,U4,U5,U6,
        B0,B1,B2,B3,B4,B5,B6,
        E0,E1,E2,E3,E4,E5,E6
    }
    
    
    public CallIF prepare(CallExecutor executor) {
		callExecutor = (CallExecutorAsync) executor;
		// deviceIdentifier = callExecutor.getNextIdentifierInRandom();//"10006660001";
		// sessionID = DomainObjectUtils.generateSessionID(callExecutor.generateSessionID());
		deviceIdentifier = callExecutor.getNextIdentifierInSequence();// "1000006660001";
		sessionID = callExecutor.generateSessionIDWithIdentifier(deviceIdentifier);

		if (logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, Thread.currentThread().getName() + "["
					+ Thread.currentThread().getId() + "] call... executor:"
					+ executor);
		}
      
      return this;
    }

    public static void initialize() {
		logger.info("initialize spr connection for CallImpl6JAsync ......");
		sessionPool = new ClusterSessionPool();
		logger.info("initialize session connection for CallImpl6JAsync ......");

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
        
        _stateCall = StateCall.begining;
        
        OcsSession ocsSession = callExecutor.generateSession(sessionID, deviceIdentifier);

        /*------------------------------- Message In ---------------------------------------------*/
        /* session */
        _stateDBBegin = StateDB.B0;
		ocsSessionDAO.insert(ocsSession, this);
        
        return this;
    }
    
    
    public CallIF update() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call update... sessionID:" + sessionID + " device identifier:" + deviceIdentifier);
        }
        _stateCall = StateCall.updating;
        /*------------------------------- Message In ---------------------------------------------*/
        /* session */
        _stateDBUpdate = StateDB.U0;
        ocsSessionDAO.findByPrimaryKey(sessionID, OcsSession.class, this);
        
        
        return this;
    }
    
  
    public CallIF end() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, Thread.currentThread().getName() + "[" + Thread.currentThread().getId()
                    + "] call end... sessionID:" + sessionID + " device identifier:" + deviceIdentifier);
        }

        _stateCall = StateCall.ending;
        /*------------------------------- Message In ---------------------------------------------*/
        /* session */
        _stateDBEnd = StateDB.E0;
        ocsSessionDAO.findByPrimaryKey(sessionID, OcsSession.class, this);

        
        return this;
    }
  
    
    public void run() {
    	try {
    		if (future != null) {
    			callback(0, this.future);
    			return;
    		}
			
			logger.warning("No response received!!!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Invalid call status " + _stateCall + ",Begin: " +_stateDBBegin + ",Update: " +_stateDBUpdate+ ",End: " +_stateDBEnd + " in callback!");
			callExecutor.callTransactionFinished(-100);
			_stateCall = StateCall.idle;
			_stateDBBegin = StateDB.idle;
			_stateDBUpdate = StateDB.idle;
			_stateDBEnd = StateDB.idle;
			
		}
    }
    
    private Subscriber subscriber;
    private OcsSession endOcsSession;
    
    private void resetLocalVars() {
    }
    
	
	public CallAsyncIF callback(int resultCode, Object resultData) {
		switch (_stateCall) {
			case begining:
				switch (_stateDBBegin) {
					case B0:
						/* device identifier */
						_stateDBBegin = StateDB.B1;
						deviceIdentifierDAO.findByPrimaryKey(DEVICE + this.deviceIdentifier, DeviceIdentifier.class, this);
						break;
					case B1:
						DeviceIdentifier deviceIdentifierJ = (DeviceIdentifier)resultData;
						String subscriberId = deviceIdentifierJ.getSubscriberId();
						
						_stateDBBegin = StateDB.B2;
						subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class, this);
						break;
					case B2:
						subscriber = (Subscriber)resultData;
						
				        /*------------------------------- Message Out ---------------------------------------------*/
				        OcsSession ocsSession = this.callExecutor.generateSession(this.sessionID, this.deviceIdentifier);
				        _stateDBBegin = StateDB.B3;
				        ocsSessionDAO.update(ocsSession, this);
						break;
					case B3:
						Subscription[] subscriptions = subscriber.getDevice().getSubscription();
				        Subscription subscription = subscriptions[0];
						subscription.getSubscriptionQuota().setQuotaReservation(subscription.getSubscriptionQuota().getQuotaReservation() + 150);
						subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
						subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
						_stateDBBegin = StateDB.B4;
						subscriberDAO.update(subscriber, this);
						
						break;
					case B4:
		        		resetLocalVars();
		        		_stateDBBegin = StateDB.idle;
		        		_stateCall = StateCall.idle;
		        		callExecutor.callTransactionFinished(RESULT_CODE_SUCCESS);
//		        		System.out.println("device::" + this.deviceIdentifier + " beginned"); //TODO 
						break;
				}
				break;
			case updating:
				switch (_stateDBUpdate) {
					case U0:
						OcsSession ocsSession = (OcsSession)resultData;

				        /* device identifier */
						_stateDBUpdate = StateDB.U1;
						deviceIdentifierDAO.findByPrimaryKey(DEVICE + this.deviceIdentifier, DeviceIdentifier.class, this);
						break;
					case U1:
						DeviceIdentifier deviceIdentifierJ = (DeviceIdentifier)resultData;
						String subscriberId = deviceIdentifierJ.getSubscriberId();
						
						/* subscriber */
						_stateDBUpdate = StateDB.U2;
						subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class, this);
						
						break;
					case U2:
						subscriber = (Subscriber)resultData;
						
				        /*------------------------------- Message Out ---------------------------------------------*/
				        OcsSession ocsSession1 = callExecutor.generateSession(sessionID, deviceIdentifier);
				        _stateDBUpdate = StateDB.U3;
				        ocsSessionDAO.update(ocsSession1, this);
						
						break;
					case U3:
						Subscription[] subscriptions = subscriber.getDevice().getSubscription();
				        Subscription subscription = subscriptions[0];
						subscription.getSubscriptionQuota().setQuotaBalance(subscription.getSubscriptionQuota().getQuotaBalance() - 120);
						subscription.getSubscriptionQuota().setQuotaReservation(subscription.getSubscriptionQuota().getQuotaReservation() - 150);
						subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
						subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
						_stateDBUpdate = StateDB.U4;
						subscriberDAO.update(subscriber, this);
						break;
					case U4:
			            resetLocalVars();
			            _stateDBUpdate = StateDB.idle;
			            _stateCall = StateCall.idle;
			            callExecutor.callTransactionFinished(RESULT_CODE_SUCCESS);
//			            System.out.println("device::" + this.deviceIdentifier + " updated"); //TODO 
						break;
				}
				break;
			case ending:
				switch (_stateDBEnd) {
					case E0:
						endOcsSession = (OcsSession)resultData;
						
						/* device identifier */
						_stateDBEnd = StateDB.E1;
						deviceIdentifierDAO.findByPrimaryKey(DEVICE + this.deviceIdentifier, DeviceIdentifier.class, this);
						
						break;
					case E1:
						DeviceIdentifier deviceIdentifierJ = (DeviceIdentifier)resultData;
						String subscriberId = deviceIdentifierJ.getSubscriberId();
						
						/* subscriber */
						_stateDBEnd = StateDB.E2;
						subscriberDAO.findByPrimaryKey(subscriberId, Subscriber.class, this);
						break;
					case E2:
						subscriber = (Subscriber)resultData;
						
						ocsSessionDAO.delete(endOcsSession, null);
						
						Subscription[] subscriptions = subscriber.getDevice().getSubscription();
				        Subscription subscription = subscriptions[0];
						subscription.getSubscriptionQuota().setQuotaBalance(subscription.getSubscriptionQuota().getQuotaBalance() - 120);
						subscription.getSubscriptionCounter().setTotalUsage(subscription.getSubscriptionCounter().getTotalUsage() + 1000000);
						subscriber.getCounter().setTotalUsage(subscriber.getCounter().getTotalUsage() + 100000);
						_stateDBEnd = StateDB.E3;
						subscriberDAO.update(subscriber, this);
						break;
					case E3:	
						resetLocalVars();
						_stateDBEnd = StateDB.idle;
						_stateCall = StateCall.idle;
						callExecutor.callTransactionFinished(RESULT_CODE_SUCCESS);
//						System.out.println("device::" + this.deviceIdentifier + " ended"); //TODO 
						break;
				}
				break;
			}
		
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
            ",stateCall=" + _stateCall +
            ", stateDBBegin=" + _stateDBBegin +
            '}';
    }
}
