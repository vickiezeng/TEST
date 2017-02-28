/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.cases
** Date: 5/30/12				Time: 10:00 AM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.cases;

import java.util.Properties;
import java.util.logging.Logger;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.DeviceIdentifier;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.Subscriber;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.DeviceIdentifierDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.OcsSessionDAO;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao.SubscriberDAO;
import com.hp.snap.evaluation.imdb.business.common.CallExecutor;
import com.hp.snap.evaluation.imdb.business.common.CallIF;
import com.hp.snap.evaluation.imdb.business.common.CallService;
import com.hp.snap.evaluation.imdb.business.common.data.SNAPDataGenerator;

/**
 *
 */
public class CallImpl4JPerf implements CallIF
{
    private static Logger logger = Logger.getLogger(CallImpl4JPerf.class.getName());
	private CallExecutor _executor;
	private SNAPDataGenerator.SprDataHolder _spr;

	private CallExecutor callExecutor;

    private static ClusterSessionPool sessionPool;


//    public static Map<Class<?>, DaoBase> daoFactory = new HashMap<Class<?>, DaoBase>();
    private static OcsSessionDAO ocsSessionDAO;
    private static DeviceIdentifierDAO deviceIdentifierDAO;
    private static SubscriberDAO subscriberDAO;
    

	public static void initialize() {
		logger.info("initialize spr connection for CallImpl4J ......");
		sessionPool = new ClusterSessionPool();
		logger.info("initialize session connection for CallImpl4J ......");

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
	
	public CallIF prepare(CallExecutor executor)
	{
		_executor = executor;
		String deviceIdentifer = _executor.getNextIdentifierInSequence();
		_spr = _executor.generateOneSPRData(Long.parseLong(deviceIdentifer));
		return this;
	}
	
	public CallIF begin()
	{
		try
		{
			long start = System.nanoTime();
			subscriberDAO.fromJsonDocument(subscriberDAO.toJsonDocument(_spr.subscriber), Subscriber.class);
			deviceIdentifierDAO.fromJsonDocument(deviceIdentifierDAO.toJsonDocument(_spr.deviceIdentifer), DeviceIdentifier.class);
			System.out.println(Thread.currentThread().getName() + " : json " + (System.nanoTime() - start)/1000);
			
			start = System.nanoTime();
			subscriberDAO.fromRawJsonDocument(subscriberDAO.toRawJsonDocument(_spr.subscriber), Subscriber.class);
			deviceIdentifierDAO.fromRawJsonDocument(deviceIdentifierDAO.toRawJsonDocument(_spr.deviceIdentifer), DeviceIdentifier.class);
			System.out.println(Thread.currentThread().getName() + " : raw " + (System.nanoTime() - start)/1000);
			
		}
		finally {
			_spr = null;
			_executor = null;
		}
		return this;
	}

	public CallIF update()
	{
		return this;
	}

	public CallIF end()
	{
		return this;
	}

	public String getSessionID()
	{
		return null;
	}
	
}