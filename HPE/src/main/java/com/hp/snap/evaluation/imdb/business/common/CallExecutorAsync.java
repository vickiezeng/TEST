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

import java.util.logging.Level;

/**
 *
 */
public class CallExecutorAsync extends CallExecutor
{
	/**
	 * Constructor
	 *
	 * @param duration       call duration in seconds.
	 * @param updateInterval call update interval in seconds
	 */
	public CallExecutorAsync(CallService service, CallAsyncIF callImpl, int duration, int updateInterval)
	{
		super(service, callImpl, duration, updateInterval);
		_callAsyncImpl = callImpl;
	}

	public CallExecutorAsync call() throws Exception
	{
		if (_waittingAsyncResult)
			throw new IllegalStateException("Illegal State, receive a transaction while waitting async result!");
		_waittingAsyncResult = true;
		long now = System.currentTimeMillis();
		_lastWaittingAsyncTime = now;
		long tEnd = _timeBegin + _duration;
		switch (_state)
		{
			case wait_start:
				_timeBegin = now;
				//Statistic
				_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_BEGIN);
				//start a call
				_callAsyncImpl.begin();
				//
				_statPendingCallCount.getAndDecrement();
				if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is starting.");
				//
				long pendingTime = _timeBegin - _timePrepare;
				_statPendingTimeTotal += pendingTime;
				if (pendingTime > _statPendingTimeMax) _statPendingTimeMax = pendingTime;
				break;
			case wait_update:
				if (now < tEnd)
				{
					//Statistic
					_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_UPDATE);
					//update
					_callAsyncImpl.update();
					if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is updating.");
				}
				else
				{
					//Statistic
					_service.getStatisticCall().requestSent(StatisticCall.TRANSACTION_END);
					//end
					_callAsyncImpl.end();
					if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is ending.");
				}
				break;
			default:
				throw new IllegalStateException("Invalid CallSession " + this);
		}
		return this;
	}

	private CallAsyncIF _callAsyncImpl;

	private boolean _waittingAsyncResult;
	private long _lastWaittingAsyncTime;

	/**
	 * Call back
	 *
	 * @param resultCode 0-success, otherwise faild
	 */
	public void callTransactionFinished(int resultCode)
	{
		if (!_waittingAsyncResult)
			throw new IllegalStateException("Illegal State, receive a callback while not waitting async result!");
		long nextTime = -1;
		long tEnd = _timeBegin + _duration;
		switch (_state)
		{
			case wait_start:
				//Statistic
				_service.getStatisticCall().sessionCreated();
				_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_BEGIN, (int) (System.currentTimeMillis() - _lastWaittingAsyncTime), resultCode);
				//
				if (resultCode == StatisticCall.RESULT_SUCCESS)
				{
					nextTime = (_duration < _updateInterval) ? _timeBegin + _duration : _timeBegin + _updateInterval;
					_state = State.wait_update;
					if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is started.");
				}
				else
				{
					endCall(resultCode);
				}
				break;
			case wait_update:
				if (_lastWaittingAsyncTime < tEnd)
				{
					//Statistic
					_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_UPDATE, (int) (System.currentTimeMillis() - _lastWaittingAsyncTime), resultCode);
					//
					if (resultCode == StatisticCall.RESULT_SUCCESS)
					{
						nextTime = _lastWaittingAsyncTime + _updateInterval;
						if (nextTime > tEnd) nextTime = tEnd;
						if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is updated.");
					}
					else
					{
						endCall(resultCode);
					}
				}
				else
				{
					//Statistic
					_service.getStatisticCall().answerReceived(StatisticCall.TRANSACTION_END, (int) (System.currentTimeMillis() - _lastWaittingAsyncTime), resultCode);
					//
					endCall(resultCode);
				}
				break;
			default:
				throw new IllegalStateException("Invalid CallSession " + this);
		}
		if (nextTime > 0)
		{
			_service.schedule(this, nextTime - _lastWaittingAsyncTime);
			_lastNextTime = nextTime;
		}
		_waittingAsyncResult = false;
	}

	private void endCall(int resultCode)
	{
		_state = State.ended;
		_service.getStatisticCall().sessionClosed(resultCode, _lastWaittingAsyncTime - _timeBegin);//real duration
		//_service.callEnded(this);
		if (_logger.isLoggable(Level.FINE)) _logger.fine("Call " + toString() + " is ended with result " + resultCode);
		_callAsyncImpl = null;//clear
		_service.getDataGenerator().returnSession(_sessionInPool);

	}
}
