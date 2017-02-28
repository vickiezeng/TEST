/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business
** Date: 5/20/12				Time: 3:32 PM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.OcsSession;
import com.hp.snap.evaluation.imdb.business.common.data.SNAPDataGenerator;

/**
 *
 */
public class CallExecutor implements Callable<CallExecutor>
{
    private static Logger logger = Logger.getLogger(CallExecutor.class.getName());

	/**
	 * Constructor
	 *
	 * @param duration       call duration in seconds.
	 * @param updateInterval call update interval in seconds
	 */
	public CallExecutor(CallService service, CallIF callImpl, int duration, int updateInterval)
	{
		_service = service;
		_duration = duration * 1000;
		_updateInterval = updateInterval * 1000;
		//Statistic
		long now = System.currentTimeMillis();
		_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_PREPARE);
		//
		_callImpl = callImpl;
		_callImpl.prepare(this);
		//Statistic
		_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_PREPARE, (int) (System.currentTimeMillis() - now), StatisticCall.RESULT_SUCCESS);
		_statPendingCallCount.getAndIncrement();
		if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is created.");
		_timePrepare = now;
	}

	public CallExecutor call() throws Exception
	{
		long now = System.currentTimeMillis();
		long nextTime;
		long tEnd = _timeBegin + _duration;
		switch (_state)
		{
			case wait_start:
				_timeBegin = now;
				//Statistic
				_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_BEGIN);
				//start a call
				try
				{
					_callImpl.begin();
				}
				catch (Throwable e)
				{
                logger.log(Level.WARNING, "begin()", e);
				}
				//Statistic
				_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_BEGIN, (int) (System.currentTimeMillis() - now), StatisticCall.RESULT_SUCCESS);
				_service.getStatisticCall().sessionCreated();
				//
				nextTime = (_duration < _updateInterval) ? now + _duration : now + _updateInterval;
				_state = State.wait_update;
				_statPendingCallCount.getAndDecrement();
				if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is begun.");
				//
				long pendingTime = _timeBegin - _timePrepare;
				_statPendingTimeTotal += pendingTime;
				if (pendingTime > _statPendingTimeMax) _statPendingTimeMax = pendingTime;
				break;
			case wait_update:
				//Stat
				if (_lastNextTime >= 0)
				{
					long delay = (now - _lastNextTime);
					if (delay > 0)
					{
						_statUEDelayTimeTotal = _statUEDelayTimeTotal + (now - _lastNextTime);
						if (delay > _statUEDelayTimeMax) _statUEDelayTimeMax = delay;
						_statUECount++;
					}
					else
					{
						//_logger.warning("Negative delay: " + delay);  ExtremeDB always print this
					}

				}
				//
				if (now < tEnd)
				{
					//Statistic
					_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_UPDATE);
					//update
					try
					{
						_callImpl.update();
					}
					catch (Throwable e)
					{
		                logger.log(Level.WARNING, "update()", e);
					}
					//Statistic
					_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_UPDATE, (int) (System.currentTimeMillis() - now), StatisticCall.RESULT_SUCCESS);
					//
					nextTime = now + _updateInterval;
					if (nextTime > tEnd) nextTime = tEnd;
					if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is updated.");
				}
				else
				{
					//Statistic
					_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_END);
					//end
					try
					{
						_callImpl.end();
					}
					catch (Throwable e)
					{
		                logger.log(Level.WARNING, "end()", e);
					}
					//Statistic
					_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_END, (int) (System.currentTimeMillis() - now), StatisticCall.RESULT_SUCCESS);
					//_service.getStatisticCall().sessionClosed(StatisticCall.RESULT_SUCCESS, _duration);
					_service.getStatisticCall().sessionClosed(StatisticCall.RESULT_SUCCESS, now - _timeBegin);//real duration
					//
					nextTime = -1;
					_state = State.ended;
					//_service.callEnded(this);
					if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is ended.");
					_callImpl = null;//clear
					_service.getDataGenerator().returnSession(_sessionInPool);
				}
				break;
			default:
				throw new IllegalStateException("Invalid CallSession " + this);
		}
		if (nextTime > 0)
		{
			_service.schedule(this, nextTime - now);
			_lastNextTime = nextTime;
		}
		return this;
	}

	public String generateSessionID()
	{
		return _service.getDataGenerator().generateSessionID();
	}

	public String generateSessionIDWithIdentifier(String identifier)
	{
		return _service.getDataGenerator().generateSessionIDWithIdentifier(identifier);
	}

	public OcsSession generateSession(String sessionID, String deviceIdentifer)
	{
		OcsSession session = _service.getDataGenerator().generateSession(sessionID, Long.valueOf(deviceIdentifer));
		if (_service.getDataGenerator().isUseSessionPool())
		{
			_sessionInPool = session;//if use pool, need to be saved to return while end.
		}
		return session;
	}

	protected OcsSession _sessionInPool;//Only available while using session pool

	public SNAPDataGenerator.SprDataHolder generateOneSPRData(long deviceIdentifier)
	{
		return _service.getDataGenerator().generateOneSubscriber(deviceIdentifier);
	}

	public void submitTaskToThreadpool(Callable task)
	{
		_service.submitTask(task);
	}

	protected enum State
	{
		wait_start, wait_update, ended
	}

	protected State _state = State.wait_start;
	protected long _timeBegin;
	protected long _duration, _updateInterval;
	protected CallService _service;
	private CallIF _callImpl;


	public String getApplicationProperty(String key)
	{
		return _service.getApplicationProperty(key);
	}

	public String getNextIdentifierInSequence()
	{
		return _service.getNextIdentifierInSequence();
	}

	public String getNextIdentifierInRandom()
	{
		return _service.getNextIdentifierInRandom();
	}
	// TODO delete
//	public void insertSPR2JDBC(Connection conn, SNAPDataGenerator.SprDataHolder sdh) throws SQLException
//	{
//		_service.insertSPR2JDBC(conn, sdh);
//	}

	@Override
	public String toString()
	{
		return "CallExecutor{" +
				"SessionID=" + _callImpl.getSessionID() +
				", state=" + _state +
				", duration=" + _duration / 1000 +
				", updateInterval=" + _updateInterval / 1000 +
				", timeBegin=" + new Date(_timeBegin) +
				'}';
	}

	static protected Logger _logger = Logger.getLogger(CallExecutor.class.getName());
	static protected AtomicLong _statPendingCallCount = new AtomicLong(0); //prepared but not begin
	protected long _lastNextTime;

	public long getLastNextTime()
	{
		return _lastNextTime;
	}

	static public long getPendingCallCount()
	{
		return _statPendingCallCount.get();
	}

	protected long _timePrepare;
	static protected long _statPendingTimeTotal, _statPendingTimeMax;
	static protected long _statUEDelayTimeTotal, _statUEDelayTimeMax, _statUECount;

	public static long getStatPendingTimeTotal()
	{
		return _statPendingTimeTotal;
	}

	public static long getStatPendingTimeMax()
	{
		return _statPendingTimeMax;
	}

	public static long getStatUEDelayTimeMax()
	{
		return _statUEDelayTimeMax;
	}

	public static long getStatUEDelayTimeTotal()
	{
		return _statUEDelayTimeTotal;
	}

	public static long getStatUEDelayTimeAverage()
	{
		if (_statUECount > 0) return _statUEDelayTimeTotal / _statUECount;
		else return -1;
	}
}
