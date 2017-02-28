/*

 */

package com.hp.snap.evaluation.imdb.business.common;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import com.esotericsoftware.kryo.serializers.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * @author wangbo
 */
public class StatisticCallTransaction implements Serializable
{

	String _name;

	public long _requestCount;
	public long _responseCount;
	public long _responseErrCount;
	public long _processTimeMs;
	public long _otherCallFailCount;
	public int[] _latencyTimePartitionDefinition;// {50,100,150,200,500,1000,5000};
	Map<Integer, Long> _mapTime2Counter = new HashMap<Integer, Long>();
	Map<Integer, String> _mapTime2CounterCalc = new HashMap<Integer, String>();
	Map<Integer, Long> _mapResultCode2Counter = new HashMap<Integer, Long>();//
	Set<Integer> _setSuccessResultCodes = new HashSet<Integer>();
	final private transient Lock _lock = new ReentrantLock();

	public StatisticCallTransaction(String name)
	{
		_name = name;
		_setSuccessResultCodes.add(StatisticCall.RESULT_SUCCESS);
		setTimePartition(1, 5, 10, 20, 50, 100, 200, 500, 1000, 5000);
	}

	public void reset()
	{
		_lock.lock();
		try
		{
			_requestCount = _responseCount = _processTimeMs = _otherCallFailCount = 0;
			_mapTime2Counter.clear();
			for (int i : _latencyTimePartitionDefinition)
			{
				_mapTime2Counter.put(i, 0L);
			}
			resetInterval();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void setTimePartition(int... uppers)
	{
		_latencyTimePartitionDefinition = uppers;
		for (int i : uppers)
		{
			_mapTime2Counter.put(i, 0L);
		}
	}

	/**
	 * @param latencyTime <0 if no answer, just a fail request
	 * @param resultCode
	 */
	void answerReceived(int latencyTime, int resultCode)
	{
		_lock.lock();
		try
		{
			//Latency
			if (latencyTime >= 0)
			{
				_processTimeMs += latencyTime;
				_responseCount++;
				// find time partition index
				int k = 0;
				for (int i = _latencyTimePartitionDefinition.length - 1; i >= 0; i--)
				{
					if (latencyTime >= _latencyTimePartitionDefinition[i])
					{
						k = i + 1;
						break;
					}
				}
				Integer key = (k >= _latencyTimePartitionDefinition.length) ? Integer.MAX_VALUE : _latencyTimePartitionDefinition[k];
				Long lValue = _mapTime2Counter.get(key);
				lValue = (lValue == null) ? 1L : lValue + 1;
				_mapTime2Counter.put(key, lValue);
				//interval latency
				_currentInterval._latencyMin = (_currentInterval._latencyMin < 0) ? latencyTime : Math.min(_currentInterval._latencyMin, latencyTime);
				_currentInterval._latencyMax = (_currentInterval._latencyMax < 0) ? latencyTime : Math.max(_currentInterval._latencyMax, latencyTime);
			}
			//Result-Code
			if (!_setSuccessResultCodes.contains(resultCode))
				_responseErrCount++;//record success other than 2001
			if (resultCode != StatisticCall.RESULT_SUCCESS)
			{
				Long result = _mapResultCode2Counter.get(resultCode);
				result = (result == null) ? 1L : result + 1;
				_mapResultCode2Counter.put(resultCode, result);
			}
		}
		finally
		{
			_lock.unlock();
		}
	}


	public int[] getLatencyTimePartitionDefinition()
	{
		return _latencyTimePartitionDefinition;
	}

	public Map<Integer, Long> getMapResultCode2Counter()
	{
		return _mapResultCode2Counter;
	}

	public Map<Integer, Long> getMapTime2Counter()
	{
		return _mapTime2Counter;
	}

	public Map<Integer, String> getMapTime2CounterCalc()
	{
		return _mapTime2CounterCalc;
	}

	public void setMapTime2CounterCalc(Map<Integer, String> map)
	{
		_mapTime2CounterCalc = map;
	}

	public String getName()
	{
		return _name;
	}

	public long getProcessTimeMs()
	{
		return _processTimeMs;
	}

	public long getRequestCount()
	{
		return _requestCount;
	}

	public long getResponseCount()
	{
		return _responseCount;
	}

	public long getResponseErrCount()
	{
		return _responseErrCount;
	}


	@Override
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.printf("\t\t=== Message:%s(%s) ===\n", getName(), super.toString());
		pw.printf("\t\t  Request=%d\n", _requestCount);
		pw.printf("\t\t  OtherCallFailCount=%d\n", _otherCallFailCount);
		pw.printf("\t\t  Response=%d\n", _responseCount);
		pw.printf("\t\t  ResponseError=%d\n", _responseErrCount);
		pw.printf("\t\t  ProcessTime(ms)=%d\n", _processTimeMs);
		if (_responseCount > 0) pw.printf("\t\t  AverageLatencyTime(ms)=%d\n", _processTimeMs / _responseCount);
		pw.printf("\t\t  -- Result-Code Table -- \n\t\t");
		for (int i : _mapResultCode2Counter.keySet())
			pw.printf("%d\t", i);
		pw.printf("\n\t\t");
		for (long i : _mapResultCode2Counter.values())
			pw.printf("%d\t", i);
		pw.printf("\n");
		pw.printf("\t\t  -- Time Partition Table -- \n");
		for (int i = 0; i < _latencyTimePartitionDefinition.length; i++)
		{
			int p = _latencyTimePartitionDefinition[i];
			if (i == 0) pw.printf("\t\t       0-%dms:%d\n", p, _mapTime2Counter.get(p));
			else
				pw.printf("\t\t       %d-%dms:%d\n", _latencyTimePartitionDefinition[i - 1], p, _mapTime2Counter.get(p));
		}
		int p = _latencyTimePartitionDefinition[_latencyTimePartitionDefinition.length - 1];
		pw.printf("\t\t       %d-... ms:%d\n", p, _mapTime2Counter.get(p));
		if (_lastInterval != null)
		{
			pw.printf("\t\t  -- Interval Latency Time at %s during %d seconds, %d messages. -- \n"
					, new Date(_lastInterval._beginTime)
					, (_lastInterval._endTime - _lastInterval._beginTime) / 1000
					, _lastInterval._responseCount);
			pw.printf("\t\t  Minimal:%d ms, Average:%d ms, Maximum:%d ms.", getIntervalLatencyMin()
					, getIntervalLatencyTimeAverage(), getIntervalLatencyMax());
		}
		return sw.toString();
	}

	private static final long serialVersionUID = 6527180291709950169L;

	public long getOtherCallFailCount()
	{
		return _otherCallFailCount;
	}

	///////// The following are changed for interval lantency time by BOW ///////////
	static private class IntervalData implements Serializable
	{
		private static final long serialVersionUID = -7509724161836656441L;
		int _latencyMin = -1, _latencyMax = -1;//
		long _beginTime = System.currentTimeMillis(), _endTime;//Begin/End time for the interval
		long _processTimeMs = -1, _responseCount = -1;
		volatile long _begin_processTimeMs, _begin_responseCount;
	}

	private IntervalData _lastInterval, _currentInterval;

	public void resetInterval()
	{
		_lock.lock();
		try
		{
			_lastInterval = _currentInterval;
			if (_lastInterval != null)
			{
				_lastInterval._endTime = System.currentTimeMillis();
				_lastInterval._processTimeMs = _processTimeMs - _lastInterval._begin_processTimeMs;
				_lastInterval._responseCount = _responseCount - _lastInterval._begin_responseCount;
			}
			_currentInterval = new IntervalData();
			_currentInterval._begin_processTimeMs = _processTimeMs;
			_currentInterval._begin_responseCount = _responseCount;
		}
		finally
		{
			_lock.unlock();
		}
	}

	public int getIntervalLatencyTimeAverage()
	{
		if (_lastInterval == null) return -1;
		if (_lastInterval._responseCount < 1) return -1;
		return (int) (_lastInterval._processTimeMs / _lastInterval._responseCount);
	}

	public int getIntervalLatencyMin()
	{
		if (_lastInterval == null) return -1;
		return _lastInterval._latencyMin;
	}

	public int getIntervalLatencyMax()
	{
		if (_lastInterval == null) return -1;
		return _lastInterval._latencyMax;
	}

	////////////////////////////////////////////////////////////////////////////////
	public void setSuccessfulResultCodes(Set<Integer> codes)
	{
		if (codes != null)
		{
			for (int i : codes)
			{
				_setSuccessResultCodes.add(i);
			}
		}
		_setSuccessResultCodes.add(StatisticCall.RESULT_SUCCESS);
	}

	public void resultCode(int resultCode)
	{
		_lock.lock();
		try
		{
			Long result = _mapResultCode2Counter.get(resultCode);
			result = (result == null) ? 1L : result + 1;
			_mapResultCode2Counter.put(resultCode, result);
		}
		finally
		{
			_lock.unlock();
		}
	}

	public long getLastIntervalBeginTime()
	{
		if (_lastInterval == null) return Long.MIN_VALUE;
		return _lastInterval._beginTime;
	}

	public long getLastIntervalDuration()
	{
		if (_lastInterval == null) return Long.MIN_VALUE;
		return _lastInterval._endTime - _lastInterval._beginTime;
	}

	static class Serializer extends FieldSerializer<StatisticCallTransaction>
	{

		public Serializer(Kryo kryo)
		{
			super(kryo, StatisticCallTransaction.class);
		}

		@Override
		public void write(Kryo kryo, Output output, StatisticCallTransaction o)
		{
			o._lock.lock();
			try
			{
				super.write(kryo,output,o);
			}
			finally
			{
				o._lock.unlock();
			}
		}
		@Override
		public StatisticCallTransaction create(Kryo kryo, Input input, Class<StatisticCallTransaction> type)
		{
			return new StatisticCallTransaction("");
		}
	}
}
