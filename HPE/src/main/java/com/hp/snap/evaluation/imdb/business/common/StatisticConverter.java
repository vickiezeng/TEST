/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common
** Date: 5/21/12				Time: 8:18 PM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Convert ser to csv
 */
public class StatisticConverter
{
	public StatisticConverter(File fIn, File fOut)
	{
		_fileIn = fIn;
		_fileOut = fOut;
		//
		_kyro = new Kryo();
		_kyro.register(StatisticCall.class, new StatisticCall.Serializer(_kyro));
		_kyro.register(StatisticCallTransaction.class, new StatisticCallTransaction.Serializer(_kyro));
	}

	/*	public File convert() throws Exception
	{
		FileWriter fw = new FileWriter(_fileOut);
		fw.write(getHeader().toString());
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_fileIn));
		StatisticCall o0 = null;
		for (Object o = ois.readObject(); o != null; o = ois.readObject())
		{
			if (!(o instanceof StatisticCall)) break;
			StatisticCall o1 = (StatisticCall) o;
			fw.write(getRow(o1, o0).toString());
			o0 = o1;
			System.out.print('.');
		}
		ois.close();
		fw.close();
		System.out.println();
		return _fileOut;
	}*/
	private Kryo _kyro;

	public File convert() throws Exception
	{

		FileWriter fw = new FileWriter(_fileOut);
		fw.write(getHeader().toString());
		Input ois = new Input(new FileInputStream(_fileIn));

		StatisticCall o0 = null;

/*
		for (Object o = _kyro.readClassAndObject(ois); o != null; o = _kyro.readClassAndObject(ois))
		{
			if (!(o instanceof StatisticCall)) break;
			StatisticCall o1 = (StatisticCall) o;
			fw.write(getRow(o1, o0).toString());
			o0 = o1;
			System.out.print('.');
		}
*/

		while(true)
		{
			try
			{
				Object o =_kyro.readObject(ois,StatisticCall.class);
				StatisticCall o1 = (StatisticCall) o;
				fw.write(getRow(o1, o0).toString());
				o0 = o1;
				System.out.print('.');
			}
			catch (KryoException e)
			{
				break;
			}
		}
		ois.close();
		fw.close();
		System.out.println();
		return _fileOut;
	}

	private StringBuilder getHeader()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Timestamp").append('|');
		sb.append("Time(Sec)").append('|');
		sb.append("ActiveSessions").append('|');
		sb.append("StartCall").append('|');
		sb.append("StartCallPerSecond").append('|');
		sb.append("SuccessfulCall").append('|');
		sb.append("OtherFailedCall").append('|');
		sb.append("TotalSuccessCallDuration").append('|');
		sb.append("AverageCallDuration").append('|');
		printHeaderTransaction(sb, StatisticCall.TRANSACTION_PREPARE);
		printHeaderTransaction(sb, StatisticCall.TRANSACTION_BEGIN);
		printHeaderTransaction(sb, StatisticCall.TRANSACTION_UPDATE);
		printHeaderTransaction(sb, StatisticCall.TRANSACTION_END);
		//
		String []kpis=StatisticCall.getAddtionalKPINames();
		if(kpis!=null)
		{
			for (String s:kpis) sb.append(s).append('|');
		}
		sb.append('\n');
		return sb;

	}


	private StringBuilder printHeaderTransaction(StringBuilder out, String name)
	{
		name = name + ".";
		//Prepare
		out.append(name).append("RequestCount").append('|');
		out.append(name).append("RequestPerSecond").append('|');
		out.append(name).append("OtherFailCount").append('|');
		out.append(name).append("ResponseCount").append('|');
		out.append(name).append("ResponseError").append('|');
		out.append(name).append("ProcessTimeMs").append('|');
		out.append(name).append("AverageLatencyTimeMs").append('|');
		//Prepare.setTimePartition(new int[]{1, 5, 10, 20, 50, 100, 200, 500, 1000, 5000});
		out.append(name).append("P0-1ms").append('|');
		out.append(name).append("P1-5ms").append('|');
		out.append(name).append("P5-10ms").append('|');
		out.append(name).append("P10-20ms").append('|');
		out.append(name).append("P20-50ms").append('|');
		out.append(name).append("P50-100ms").append('|');
		out.append(name).append("P100-200ms").append('|');
		out.append(name).append("P200-500ms").append('|');
		out.append(name).append("P500-1000ms").append('|');
		out.append(name).append("P1000-5000ms").append('|');
		out.append(name).append("P5000-ms").append('|');
		//Prepare.Interval
		out.append(name).append("IntervalBeginTime").append('|');
		out.append(name).append("IntervalDuration").append('|');
		out.append(name).append("IntervalLatencyMin").append('|');
		out.append(name).append("IntervalLatencyAvg").append('|');
		out.append(name).append("IntervalLatencyMax").append('|');
		return out;
	}

	private StringBuilder getRow(StatisticCall o1, StatisticCall o0)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(time2string(o1.getTimestampCurrent())).append('|');
		sb.append((o1.getTimestampCurrent() - o1.getTimestampBegin()) / 1000).append('|');
		sb.append(o1.getActiveSession()).append('|');
		sb.append(o1.getStartCallCounter()).append('|');
		long value = o1.getStartCallCounter() - ((o0 == null) ? 0 : o0.getStartCallCounter());
		long time = o1.getTimestampCurrent() - ((o0 == null) ? o1.getTimestampBegin() : o0.getTimestampCurrent());
		sb.append((time > 0) ? 1000 * value / time : "ERR").append('|');
		sb.append(o1.getSuccessCallCounter()).append('|');
		sb.append(o1.getOtherCallFailedCounter()).append('|');
		sb.append(o1.getTotalSuccessCallDuration()).append('|');
		sb.append(o1.getAverageDurationForSuccessfulCall()).append('|');
		//
		printRowTransaction(sb, o1, o0, StatisticCall.TRANSACTION_PREPARE);
		printRowTransaction(sb, o1, o0, StatisticCall.TRANSACTION_BEGIN);
		printRowTransaction(sb, o1, o0, StatisticCall.TRANSACTION_UPDATE);
		printRowTransaction(sb, o1, o0, StatisticCall.TRANSACTION_END);
		//
		long []kpis=o1.getAddtionalKPIs();
		if(kpis!=null)
		{
			for(long v:kpis) sb.append(v).append('|');
		}

		//
		sb.append('\n');
		return sb;
	}

	private StringBuilder printRowTransaction(StringBuilder out, StatisticCall o1, StatisticCall o0, String name)
	{
		StatisticCallTransaction t1 = o1.getMapMessageName2Stat().get(name);
		StatisticCallTransaction t0 = (o0 != null) ? o0.getMapMessageName2Stat().get(name) : null;
		//Prepare
		out.append(t1.getRequestCount()).append('|');

		long value = t1.getRequestCount() - ((t0 == null) ? 0 : t0.getRequestCount());
		long time = o1.getTimestampCurrent() - ((o0 == null) ? o1.getTimestampBegin() : o0.getTimestampCurrent());
		out.append((time > 0) ? 1000 * value / time : "ERR").append('|');

		out.append(t1.getOtherCallFailCount()).append('|');
		out.append(t1.getResponseCount()).append('|');
		out.append(t1.getResponseErrCount()).append('|');
		out.append(t1.getProcessTimeMs()).append('|');
		long avg = (t1.getResponseCount() > 0) ? t1.getProcessTimeMs() / t1.getResponseCount() : 0;
		out.append(avg).append('|');
		//Prepare.setTimePartition(new int[]{1, 5, 10, 20, 50, 100, 200, 500, 1000, 5000});
		Map<Integer, Long> map = t1.getMapTime2Counter();
		for (int i : t1.getLatencyTimePartitionDefinition())
		{
			out.append(map.get(i)).append('|');
		}
		out.append(map.get(Integer.MAX_VALUE) == null ? 0 : map.get(Integer.MAX_VALUE)).append('|');

		//Prepare.Interval
		value = t1.getLastIntervalBeginTime();
		out.append((value >= 0) ? time2string(value) : "ERR").append('|');
		value = t1.getLastIntervalDuration();
		out.append((value >= 0) ? value : "ERR").append('|');
		value = t1.getIntervalLatencyMin();
		out.append((value >= 0) ? value : 0).append('|');
		value = t1.getIntervalLatencyTimeAverage();
		out.append((value >= 0) ? value : 0).append('|');
		value = t1.getIntervalLatencyMax();
		out.append((value >= 0) ? value : 0).append('|');
		return out;
	}


	private File _fileIn, _fileOut;

	static private String time2string(long time)
	{
		return TIME_FORMAT.format(new Date(time));
	}

	static final private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");

	public static void main(String[] args) throws Exception
	{
		com.esotericsoftware.minlog.Log.TRACE();
		File fIn = new File("logs/1.ser");
		File fOut = new File("logs/1.csv");
		StatisticConverter app = new StatisticConverter(fIn, fOut);
		app.convert();
	}

}
