package com.hp.snap.evaluation.imdb.business.common;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Traffic Statistician
 */
public class Statistician
{
	private StatisticCall _stat;

	private long _timeLastGetStat, _timeStatTerminate;

	private CallService _callService;

	public Statistician(CallService callService)
	{
		_callService = callService;
		_stat = callService.getStatisticCall();
	}

	private long _collectInterval = 5000L;

	public void setCollectIntervalSec(long collectIntervalSec)
	{
		_collectInterval = collectIntervalSec * 1000L;
	}

	public String getName()
	{
		return _stat.getName();
	}

	public void start()
	{
		startTask();
	}

	public void stop()
	{
		long now = System.currentTimeMillis();
		if (_stat != null)
		{
			_stat.setTimestampCurrent(now);
			_stat.setTimestampEnd(now);
		}
		if (_timer != null) _timer.cancel();
		if (_statWriter != null)
		{
			_statWriter.write(_stat);
			_statWriter.close();
		}
		_timeStatTerminate = now;
		_callService.notifyCallCompleted();
	}

	public boolean isCompleted()
	{
		return (_stat.getActiveSession() > 0);
	}

	public long getStartedCall()
	{
		return _stat.getStartCallCounter();
	}

	public long getActiveSession()
	{
		return _stat.getActiveSession();
	}


	private void startTask()
	{
		_timer = new Timer("StatisticalWriter");

		try
		{
			statReset();
			// Create file
//			File fStat = new File("logs/" + _stat.getName() + "-" + date2string(new Date()) + "-StatisticCall.ser");
			String logFolder = CallService.getInstance().getApplicationProperty("ClientApp.LogFolder");
			if(logFolder == null || logFolder.isEmpty()){
				logFolder = "logs/";
			}
			File fStat = new File(logFolder + "/" + _stat.getName() + "-" + date2string(new Date()) + "-StatisticCall.ser");
			fStat.getParentFile().mkdirs();
			_statWriter = new StatisticalWriter(new FileOutputStream(fStat));
			_stat.setTimestampBegin(System.currentTimeMillis());

		}
		catch (IOException e)
		{
			_logger.log(Level.WARNING, "Can NOT start StatisticalWriter", e);
			throw new IllegalArgumentException(e);
		}

		// start
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				if (_statWriter == null) return;
				long now = System.currentTimeMillis();
				if (_callService.isRunning() || (_timeLastGetStat <= _timeStatTerminate))
				{
					_callService.statisticUpdateKPIs(now);
					_stat.setTimestampCurrent(now);
					_timeLastGetStat = now;
					_stat.setTimestampEnd(_timeStatTerminate);
					_statWriter.write(_stat);
					_stat.resetInterval();
					if ((_stat.getStartCallCounter() == _callService.getCallCount()) && (!_reportedCallStarted))
					{
						long t = now - _stat.getTimestampBegin();
						long caps = 0;
						if (t > 0) caps = _callService.getCallCount() * 1000L / t;
						_logger.info("Call service " + _callService + " started all of " + _callService.getCallCount() + " calls. Rough call per second: " + caps);
						_reportedCallStarted = true;
					}
				}
				else
				{
					_logger.info("Call service " + _callService + " is completed.");
					stop();
				}
			}
		};

		_timer.schedule(task, _collectInterval, _collectInterval);

	}

	private boolean _reportedCallStarted;


	private Timer _timer;


	private StatisticalWriter _statWriter;


	public void statReset()
	{
		if (_stat != null) _stat.reset(); // reset StatisticCall values
	}


	static public String date2string(Date date)
	{
		return TIME_FORMAT.format(date);
	}

	static final private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	public class StatisticalWriter
	{
		public StatisticalWriter(OutputStream osTraffic)
		{
			//com.esotericsoftware.minlog.Log.TRACE();
			_kyro = new Kryo();
			_kyro.register(StatisticCall.class, new StatisticCall.Serializer(_kyro));
			_kyro.register(StatisticCallTransaction.class, new StatisticCallTransaction.Serializer((_kyro)));
			_kryoOutput = new Output(osTraffic);
		}

		public void close()
		{
			if (_kryoOutput != null)
			{
				_kryoOutput.write(-1);//EOF
				_kryoOutput.close();
			}
			_kryoOutput = null;
		}

		public void write(StatisticCall statTraffic)
		{
			if (_kryoOutput == null) return;
			if (statTraffic == null) return;
			_kyro.writeObject(_kryoOutput, statTraffic);
		}

		//private ObjectOutputStream _osStatTraffic;
		private Kryo _kyro;
		private Output _kryoOutput;
	}

	private Logger _logger = Logger.getLogger(getClass().getName());

}
