/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business
** Date: 5/20/12				Time: 8:18 PM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 *
 */
public class StatisticCall implements Serializable
{
	static public int RESULT_SUCCESS = 0;
	static public String TRANSACTION_PREPARE = "Prepare";
	static public String TRANSACTION_BEGIN = "Begin";
	static public String TRANSACTION_UPDATE = "Update";
	static public String TRANSACTION_END = "End";
	
	static public String MSG_INSERT = "MsgInsert";
	static public String MSG_UPDATE = "MsgUpdate";
	static public String MSG_DELETE = "MsgDelete";
	static public String MSG_QUERY = "MsgQuery";

	public StatisticCall(String name)
	{
		_name = name;
		setSuccessfulResultCodes(new HashSet<Integer>());
		addMessageFormats(TRANSACTION_PREPARE, TRANSACTION_BEGIN, TRANSACTION_UPDATE, TRANSACTION_END, MSG_INSERT, MSG_UPDATE, MSG_DELETE, MSG_QUERY);
		_timestampBegin = _timestampCurrent = System.currentTimeMillis();
	}

	private String _name;
	private long _startCallCounter;
	private long _totalSuccessCallDuration;
	private long _successCallCounter;
	private long _otherCallFailedCounter;
	private Map<String, StatisticCallTransaction> _mapMessageName2Stat = new HashMap<String, StatisticCallTransaction>();
	final private transient Lock _lock = new ReentrantLock();


	public void reset()
	{
		_lock.lock();
		try
		{
			_timestampBegin = _timestampCurrent = -1L;
			_startCallCounter = _successCallCounter = _otherCallFailedCounter = 0;
			for (StatisticCallTransaction sm : _mapMessageName2Stat.values())
				sm.reset();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void addMessageFormats(String... messageNames)
	{
		for (String messageName : messageNames)
			_mapMessageName2Stat.put(messageName, new StatisticCallTransaction(messageName));
	}

	public void sessionCreated()
	{
		_lock.lock();
		try
		{
			_startCallCounter++;
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void sessionClosed(int resultCode, long duration)
	{
		_lock.lock();
		try
		{
			if (_setSuccessResultCodes.contains(resultCode)) _totalSuccessCallDuration += duration;
			sessionClosed(resultCode);
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void sessionClosed(int resultCode)
	{
		_lock.lock();
		try
		{
			if (_setSuccessResultCodes.contains(resultCode)) _successCallCounter++;
			else _otherCallFailedCounter++;//record success other than 0
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void requestSent(String msgType)
	{
		StatisticCallTransaction statMsg = _mapMessageName2Stat.get(msgType);
		if (statMsg == null) throw new IllegalArgumentException("Unsupport message type " + msgType);
		_lock.lock();
		try
		{
			statMsg._requestCount++;
		}
		finally
		{
			_lock.unlock();
		}
	}


	public void requestOtherCallFail(String msgType, int resultCode)
	{
		StatisticCallTransaction statMsg = _mapMessageName2Stat.get(msgType);
		if (statMsg == null) throw new IllegalArgumentException("Unsupport message type " + msgType);
		_lock.lock();
		try
		{
			statMsg._otherCallFailCount++;
			statMsg.resultCode(resultCode);
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void markQuery(long latencyTime) {
		answerReceived(StatisticCall.MSG_QUERY, (int)latencyTime, StatisticCall.RESULT_SUCCESS);
	}
	
	public void markInsert(long latencyTime) {
		answerReceived(StatisticCall.MSG_INSERT, (int)latencyTime, StatisticCall.RESULT_SUCCESS);
	}
	
	public void markUpdate(long latencyTime) {
		answerReceived(StatisticCall.MSG_UPDATE, (int)latencyTime, StatisticCall.RESULT_SUCCESS);
	}
	
	public void markDelete(long latencyTime) {
		answerReceived(StatisticCall.MSG_DELETE, (int)latencyTime, StatisticCall.RESULT_SUCCESS);
	}
	
	public void answerReceived(String msgType, int latencyTime, int resultCode)
	{
		StatisticCallTransaction statMsg = _mapMessageName2Stat.get(msgType);
		if (statMsg == null) throw new IllegalArgumentException("Unsupport message type " + msgType);
		statMsg.answerReceived(latencyTime, resultCode);
	}

	public Map<String, StatisticCallTransaction> getMapMessageName2Stat()
	{
		return _mapMessageName2Stat;
	}

	/**
	 * Print current thread dump in abnormal state.
	 */
	public void printThreadDump() {
		ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
		for(final ThreadInfo info : threadInfos) {
		    System.out.print(info.toString());
		}
	}

	public long getOtherCallFailedCounter()
	{
		return _otherCallFailedCounter;
	}

	public long getStartCallCounter()
	{
		return _startCallCounter;
	}

	public long getSuccessCallCounter()
	{
		return _successCallCounter;
	}

	@Override
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		printStatus(pw, true);
		return sw.toString();
	}

	public PrintWriter printStatus(PrintWriter pw, boolean includeMessage)
	{
		pw.printf("\t=== Service:%s ===\n", _name);
		pw.printf("Time Now:%s Begin:%s End:%s \n"
				, Statistician.date2string(new Date(_timestampCurrent))
				, Statistician.date2string(new Date(_timestampBegin))
				, Statistician.date2string(new Date(_timestampEnd)));
		pw.printf("\t  ActiveSession=%d\n", getActiveSession());
		if (getSuccessCallCounter() > 0)
			pw.printf("\t  AverageCallDuration=%d\n", _totalSuccessCallDuration / getSuccessCallCounter());
		pw.printf("\t  StartCall=%d\n", getStartCallCounter());
		pw.printf("\t  SuccessfulCall=%d\n", getSuccessCallCounter());
		pw.printf("\t  OtherFailedCall=%d\n", getOtherCallFailedCounter());
		pw.printf("\t  -- Result-Code Table -- \n\t");
		if (includeMessage)
		{
			for (StatisticCallTransaction sm : getMapMessageName2Stat().values())
			{
				pw.println(sm.toString());
			}
		}
		return pw;
	}

	public int getIntervalLatencyTimeAverage()
	{
		int total = 0, count = 0;
		for (StatisticCallTransaction ssm : _mapMessageName2Stat.values())
		{
			int i = ssm.getIntervalLatencyTimeAverage();
			if (i > 0)
			{
				total += i;
				count++;
			}
		}
		if (count < 1) return 0;
		return total / count;
	}

	public int getIntervalLatencyMin()
	{
		int min = Integer.MAX_VALUE;
		for (StatisticCallTransaction sm : _mapMessageName2Stat.values())
		{
			int i = sm.getIntervalLatencyMin();
			if (i < 0) continue;
			min = Math.min(i, min);
		}
		if (min == Integer.MAX_VALUE) return 0;
		return min;
	}

	public int getIntervalLatencyMax()
	{
		int max = 0;
		for (StatisticCallTransaction sm : _mapMessageName2Stat.values())
		{
			int i = sm.getIntervalLatencyMax();
			if (i < 0) continue;
			max = Math.max(i, max);
		}
		return max;
	}
	////////////////////////////////////////////////////////////////////////////////

	public long getActiveSession()
	{
		long x = _startCallCounter - _otherCallFailedCounter - _successCallCounter;
		if (x < 0)
		{
			System.err.println("Active Session Count error:" + x + " in service ");
			x = 0;
		}
		return x;
	}

	private Set<Integer> _setSuccessResultCodes = new HashSet<Integer>();

	public void setSuccessfulResultCodes(Set<Integer> codes)
	{
		if (codes != null)
		{
			for (int i : codes)
			{
				_setSuccessResultCodes.add(i);
			}
		}
		_setSuccessResultCodes.add(RESULT_SUCCESS);
		for (StatisticCallTransaction ssm : _mapMessageName2Stat.values()) ssm.setSuccessfulResultCodes(codes);
	}

	public int getAverageDurationForSuccessfulCall()
	{
		if (_successCallCounter < 1) return -1;
		return (int) (_totalSuccessCallDuration / _successCallCounter);

	}

	public long getTotalSuccessCallDuration()
	{
		return _totalSuccessCallDuration;
	}

	public String getName()
	{
		return _name;
	}

	long _timestampBegin, _timestampCurrent, _timestampEnd;

	public long getTimestampBegin()
	{
		return _timestampBegin;
	}

	public void setTimestampBegin(long timestampBegin)
	{
		_timestampBegin = timestampBegin;
	}

	public long getTimestampCurrent()
	{
		return _timestampCurrent;
	}

	public void setTimestampCurrent(long timestampCurrent)
	{
		_timestampCurrent = timestampCurrent;
	}

	public long getTimestampEnd()
	{
		return _timestampEnd;
	}

	public void setTimestampEnd(long timestampEnd)
	{
		_timestampEnd = timestampEnd;
	}

	public void resetInterval()
	{
		for (StatisticCallTransaction ssm : _mapMessageName2Stat.values())
		{
			ssm.resetInterval();
		}
	}

	static class Serializer extends FieldSerializer<StatisticCall>
	{
		public Serializer(Kryo kryo)
		{
			super(kryo, StatisticCall.class);
		}

		@Override
		public void write(Kryo kryo, Output output, StatisticCall o)
		{
			o._lock.lock();
			try
			{
				super.write(kryo, output, o);
			}
			finally
			{
				o._lock.unlock();
			}
		}

		@Override
		public StatisticCall create(Kryo kryo, Input input, Class<StatisticCall> type)
		{
			return new StatisticCall("");
		}
	}

	public long getTransactionResponseCount(Set<String> names)
	{
		long result = 0;
		for (StatisticCallTransaction ssm : _mapMessageName2Stat.values())
		{
			if ((names != null) && (!names.contains(ssm._name))) continue;
			result += ssm.getResponseCount();
		}
		return result;
	}

	public long getTransactionProcessTime(Set<String> names)
	{
		long result = 0;
		for (StatisticCallTransaction ssm : _mapMessageName2Stat.values())
		{
			if ((names != null) && (!names.contains(ssm._name))) continue;
			result += ssm.getProcessTimeMs();
		}
		return result;
	}
	////////////////////////////////////////////////
	static public void defineAddtionalKPIs(String... names)
	{
		_addtionalKPINames = names;
	}

	public void setAddtionalKPIs(long... values)
	{
		if (_addtionalKPINames == null) throw new IllegalArgumentException("Undefined defineAddtionalKPIs!");
		if (values.length != _addtionalKPINames.length)
			throw new IllegalArgumentException("setAddtionalKPIs cannot match defineAddtionalKPIs!");
		_addtionalKPIValues = values;
	}

	public long[] getAddtionalKPIs()
	{
		return _addtionalKPIValues;
	}

	long[] _addtionalKPIValues;
	static private String[] _addtionalKPINames;

	static public String[] getAddtionalKPINames()
	{
		return _addtionalKPINames;
	}
}
