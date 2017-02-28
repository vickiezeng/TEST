/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business
** Date: 5/20/12				Time: 3:25 PM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.hp.snap.evaluation.imdb.business.common.data.SNAPDataGenerator;

/**
 * Service entry for call
 */
public class CallService
{
	public static String PROP_CLUSTER_CONTACT_POINTS = "cluster.contact.points";
	public static String PROP_CLUSTER_CONTACT_PORT = "cluster.contact.port";
	public static String PROP_CLUSTER_SPR_KEYSPACE = "cluster.spr.keyspace";
	public static String PROP_CLUSTER_SESSION_KEYSPACE = "cluster.session.keyspace";
	
	/**
	 * Constructor
	 *
	 * @name service name. the configuration will be [name].properties
	 */
	public CallService(String name)
	{
		if (INSTANCE != null)
			throw new IllegalArgumentException("CallService " + INSTANCE._name + " has already been created, just one instance is allowed in one application.");
		_name = name;
		INSTANCE = this;
	}

	private static CallService INSTANCE = null;

	static public CallService getInstance()
	{
		if (INSTANCE == null) throw new IllegalArgumentException("CallService was not created!");
		return INSTANCE;
	}

	/**
	 * Configure with [name].properties
	 */
	public Properties configure()
	{
		try
		{
			String cfgFile = _name + ".properties";
			_logger.info("Loading configuration from " + cfgFile + " for " + this + "...");
			InputStream fis = getClass().getClassLoader().getResourceAsStream(cfgFile);
            if (null == fis) {
                // fis = Files.newInputStream(Paths.get(System.getProperty("user.dir") + "/conf/" + cfgFile));
                fis = new FileInputStream(new File(System.getProperty("user.dir") + "/conf/" + cfgFile));
            }
			_appProperties.load(fis);
			fis.close();
			//Logging
			fis = getClass().getClassLoader().getResourceAsStream(cfgFile);
			if (null == fis) {
                // fis = Files.newInputStream(Paths.get(System.getProperty("user.dir") + "/conf/" + cfgFile));
                fis = new FileInputStream(new File(System.getProperty("user.dir") + "/conf/" + cfgFile));
            }
			LogManager.getLogManager().readConfiguration(fis);
			fis.close();
			String fLog = _appProperties.getProperty("java.util.logging.FileHandler.pattern");
			if (fLog != null)
			{
				(new File(fLog)).getParentFile().mkdirs();
			}
			//
			int threadCount = Integer.parseInt(_appProperties.getProperty("ThreadPoolSize"));
			_taskExecutor = new STFThreadPoolExecutor(threadCount );

			StringTokenizer st = new StringTokenizer(_appProperties.getProperty("CallDuration"), "-");
			_callDurationMin = Integer.parseInt(st.nextToken());
			_callDurationMax = Integer.parseInt(st.nextToken());
			st = new StringTokenizer(_appProperties.getProperty("CallUpdateInterval"), "-");
			_callUpdateIntervalMin = Integer.parseInt(st.nextToken());
			_callUpdateIntervalMax = Integer.parseInt(st.nextToken());
			int expectCallPerSecond = Integer.parseInt(_appProperties.getProperty("CallPerSecond"));
			if (expectCallPerSecond > 0)
			{
				_enableCallRateControl = true;
				setCAPS(expectCallPerSecond);
			}
			else
			{
				_enableCallRateControl = false;
				String s = _appProperties.getProperty("MaxPendingCallCount");
				if (s != null) _maxPendingCallCount = Integer.parseInt(s);
				_logger.info("Using automatic call rate control with MaxPendingCallCount " + _maxPendingCallCount + " for service " + _name);
			}
			_callCount = Integer.parseInt(_appProperties.getProperty("CallCount").trim().replace("_", ""));
			_classCallImpl = Class.forName(_appProperties.getProperty("CallImplClass"));
			_logger.info("Call implementation class is " + _classCallImpl.getName());
			_sectionIdentifer = new Section(_appProperties.getProperty("Section.Identifier"));
			_dataGenerator = new SNAPDataGenerator(_appProperties.getProperty("Section.Identifier"));
			//
			_callBegun = 0;
			_logger.info("Configured " + this + " with duration " + _callDurationMin + "-" + _callDurationMax + " sec."
					+ " update interval " + _callUpdateIntervalMin + "-" + _callUpdateIntervalMax + " sec."
					+ " time slot " + _timeSlotMs + " ns. max call per slot " + _maxCallPerSlot + ". Call count " + _callCount + ".");
			_statisticCall = new StatisticCall(_name);
			StatisticCall.defineAddtionalKPIs("TaskQueue","SchedulerQueue","PendingCalls","DelayBeginTotal","DelayUpdateEndTotal");
			_statistician = new Statistician(this);
			_statistician.setCollectIntervalSec(Integer.parseInt(_appProperties.getProperty("Statistician.CollectIntervalSec")));
			//
			String tmp=_appProperties.getProperty("AutoExitDelayWhileCompleted");
			if(tmp!=null) 	_autoExitDelayWhileCompleted=Integer.parseInt(tmp);
			if(_autoExitDelayWhileCompleted>0)
				_logger.info("STF will exit automatically while service is completed after "+_autoExitDelayWhileCompleted+" seconds.");
			else
				_logger.info("STF will not exit automatically while service is completed.");

		}
		catch (Exception e)
		{
			_logger.log(Level.SEVERE, "Configure service " + this + " ERROR:", e);
			throw new IllegalArgumentException(e);
		}
		return _appProperties;
	}
	public void statisticUpdateKPIs(long now)
	{
		_statisticCall.setAddtionalKPIs(_taskExecutor.getImmediateQueueSize()
				,_taskExecutor.getSchedulerQueueSize()
				,CallExecutor.getPendingCallCount()
		        ,CallExecutor.getStatPendingTimeTotal()
				,CallExecutor.getStatUEDelayTimeTotal());
	}

	public void start()
	{
		_isStartingCall = true;
		_statistician.start();
		if (_callbackBegin != null) _callbackBegin.run();        //
		Thread threadCall = new Thread()
		{
			@Override
			public void run()
			{
				startAllCalls();
			}
		};
		threadCall.start();
	}

	/**
	 * Exit service
	 */
	public void exit()
	{
		//_logger.info("Please waiting for "+toString()+" completion for "+_pool.getQueue().size()+" tasks...");
		if ((_statistician != null) && isRunning()) _statistician.stop();
		_taskExecutor.shutdown();
        checkRunningScheduledES.shutdownNow();
//		_poolSessionUpdate.shutdown();
//		_poolExecutor.shutdown();
		convertStatistic();
		System.exit(0);
	}

	public String getApplicationProperty(String key)
	{
		return _appProperties.getProperty(key);
	}

	public String getNextIdentifierInSequence()
	{
		return _sectionIdentifer.selectNext();
	}

    public Properties getConfig() {
        return _appProperties;
    }

	private STFThreadPoolExecutor _taskExecutor;
	//private ScheduledThreadPoolExecutor _poolSessionUpdate;//thread pool
	//private ExecutorService _poolExecutor;
	private Random _rand = new Random();
	private int _callDurationMin = 30, _callDurationMax = 120;
	private int _callUpdateIntervalMin = 10, _callUpdateIntervalMax = 60;
    private long _timeSlotMs = 20 * 1000 * 1000L;// nanosecond = 20ms
    private long _maxCallPerSlot = 5000L;// 5000 call per seconds
	private boolean _enableCallRateControl = true;
	private boolean _isStartingCall;
	private int _callCount = 10;
	private String _name;
	private Properties _appProperties = new Properties();
	private Section _sectionIdentifer;
	private Class<?> _classCallImpl;

	private void startOneCall(long delayCall)
	{
		try
		{
			if (delayCall < 0) delayCall = 0;
			//
			int duration = (_callDurationMin > 0) ? (_callDurationMin + ((_callDurationMax > _callDurationMin) ? _rand.nextInt(_callDurationMax - _callDurationMin) : 0)) : 0;
			int updateInterval = (_callUpdateIntervalMin > 0) ? (_callUpdateIntervalMin + ((_callUpdateIntervalMax > _callUpdateIntervalMin) ? _rand.nextInt(_callUpdateIntervalMax - _callUpdateIntervalMin) : 0)) : 0;
			Object callInstance = _classCallImpl.newInstance();
			CallExecutor cs;
			if (callInstance instanceof CallAsyncIF)
			{
				cs = new CallExecutorAsync(this, (CallAsyncIF) callInstance, duration, updateInterval);
			}
			else
			{
				cs = new CallExecutor(this, (CallIF) callInstance, duration, updateInterval);
			}
			//_poolSessionUpdate.schedule(cs, delayCall, TimeUnit.MILLISECONDS);
			//_poolExecutor.submit(cs);
			_taskExecutor.submitImmediate(cs);
			_callBegun++;
		}
		catch (Exception e)
		{
			_logger.log(Level.SEVERE, "Create call error!", e);
			exit();
		}
	}

	void schedule(CallExecutor cs, long delayMs)
	{
		_taskExecutor.schedule(cs, delayMs);
		//_poolSessionUpdate.schedule(cs, delayMs, TimeUnit.MILLISECONDS);
	}

	public boolean isRunning()
	{
		if (_statisticCall == null) return false;
		return _statisticCall.getActiveSession() > 0 || _isStartingCall;
	}

	private void startAllCalls()
	{
		//Auto rate control at the beginning
		int targetCAPS = _currentCAPS;
		long t0 = System.nanoTime();
		long tAutoInterval = -1;
		int deltaCAPS = -1;
		if (targetCAPS > 100)
		{
			int increaseInterval = 3000;
			String increaseIntervalStr = _appProperties.getProperty("caps.increase.interval.inms");
			if(null != increaseIntervalStr && !increaseIntervalStr.isEmpty()){
				increaseInterval = Integer.valueOf(increaseIntervalStr);
			}
//			tAutoInterval = 3 * 1000 * 1000 * 1000L;//nanoseconds = Increase per 3 second.
			tAutoInterval = increaseInterval * 1000 * 1000L;//nanoseconds = Increase per X ms.
			int i = 10;
			deltaCAPS = targetCAPS / i;
			for (i = i + 1; deltaCAPS > 20; i++)
				deltaCAPS = targetCAPS / i;//complete in 10 times
			_currentCAPS = deltaCAPS;
			_logger.info("Auto increase CAPS from " + _currentCAPS + ", increase " + deltaCAPS + " CAPS every " + tAutoInterval + "ns, target CAPS " + targetCAPS);
			setCAPS(deltaCAPS);
		}
		//
		long delayCall = 0;
		long tSlotBegin = System.nanoTime();
		int callInCurrentSlot = 0;
		_logger.info("Call Service " + _name + " is committing...");
		for (int i = 0; i < _callCount; i++)
		{
			if (!_enableCallRateControl)
			{
				startOneCall(delayCall);
				//Auto adjust based on pending call
				//int tasks = _poolSessionUpdate.getQueue().size();
				waitingIfExceedPendingCall();
				continue;
			}
			long now = System.nanoTime();
			//Auto CAPS at he beginning
			if (_currentCAPS < targetCAPS)
			{
				if ((now - t0) > tAutoInterval)
				{
					_currentCAPS += deltaCAPS;
					if (_currentCAPS > targetCAPS) _currentCAPS = targetCAPS;
					setCAPS(_currentCAPS);
					t0 = now;
					_logger.info("Auto increase CAPS to " + _currentCAPS + " of " + targetCAPS);
				}
			}
			//call rate control
			if ((now - tSlotBegin) < _timeSlotMs)
			{
				callInCurrentSlot++;
			}
			else
			{
				callInCurrentSlot = 1;
				tSlotBegin = now;
			}
			startOneCall(delayCall);
			if (callInCurrentSlot >= _maxCallPerSlot)
			{
				callInCurrentSlot = 0;
				long sleep = tSlotBegin + _timeSlotMs - System.nanoTime();
				try
				{
                    if (sleep > 0) {
                        TimeUnit.NANOSECONDS.sleep(sleep);
                    }
				}
				catch (InterruptedException e)
				{
					_logger.log(Level.WARNING, "Call rate control error", e);
				}
				tSlotBegin = System.nanoTime();
			}
			//waitingIfExceedPendingCall(); //is it required?
		}
		_isStartingCall = false;
		_logger.info("Call Service " + _name + " committed " + _callCount + " calls.");
	}

	private void waitingIfExceedPendingCall()
	{
		long calls = CallExecutor.getPendingCallCount();
		while (calls > _maxPendingCallCount)
		{
			try
			{
				Thread.sleep(1000);
				//System.out.print('#');
				calls = CallExecutor.getPendingCallCount();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private int _maxPendingCallCount = 10000;
	private long _callBegun;
	private Logger _logger = Logger.getLogger(getClass().getName());
	private StatisticCall _statisticCall;
	private Statistician _statistician;

	public StatisticCall getStatisticCall()
	{
		return _statisticCall;
	}

	public static String inputString(String prompt, String defaultValue) throws InterruptedIOException
	{
		try
		{
			if (defaultValue != null) prompt += "[" + defaultValue + "]";
			System.out.print(prompt);
			String tmp = _keyboard.readLine();
			if (tmp == null) return defaultValue;
			if (tmp.trim().length() < 1) return defaultValue;
			return tmp.trim();
		}
		catch (InterruptedIOException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			return defaultValue;
		}
	}

	private static BufferedReader _keyboard = new BufferedReader(new InputStreamReader(System.in));
	private Thread _consoleThread;

	private static ScheduledExecutorService checkRunningScheduledES = Executors.newSingleThreadScheduledExecutor();
    public void console(boolean isBackground) throws Exception {
        if (isBackground) {
            start();

            final CountDownLatch latch = new CountDownLatch(1);
            checkRunningScheduledES.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    if (!isRunning())
                        latch.countDown();
                }
            }, 1L, 1L, TimeUnit.MINUTES);

            if (!latch.await(3L, TimeUnit.HOURS)) {
                System.err.println("Call service is timeout in 3 hours!");
            }
            
            exit();

        } else {
            console();
        }
    }

	public void console() throws Exception
	{
		_consoleThread = Thread.currentThread();
		try
		{
			for (String cmd = inputString(">", null); true; cmd = inputString(">", null))
			{
				if (_consoleThread.isInterrupted()) throw new InterruptedIOException();
				try
				{
					if (cmd == null)
					{
						reportStatus();
						continue;
					}
					cmd = cmd.trim();
					if (cmd.length() < 1) continue;
					StringTokenizer stCmd = new StringTokenizer(cmd, " ");
					cmd = stCmd.nextToken();
					if ("start".equalsIgnoreCase(cmd))
					{
						start();
					}
					else if (cmd.equalsIgnoreCase("stat"))
					{
						String sArg = stCmd.hasMoreTokens() ? stCmd.nextToken() : null;
						boolean isShowAll = "all".equalsIgnoreCase(sArg);
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						_statisticCall.printStatus(pw, isShowAll);
						System.out.println(sw.toString());
					}
					else if (cmd.equalsIgnoreCase("convert"))
					{
						convertStatistic();
					}
					else if ("exit".equalsIgnoreCase(cmd))
					{
						if (isRunning())
						{
							System.out.println(toString() + " is running!");
							continue;
						}
						exit();
						break;
					}
					else if ("abort".equalsIgnoreCase(cmd))
					{
						exit();
						System.exit(-1);
						break;
					}
					else if (_cmd2Impl.containsKey(cmd))
					{
						Runnable cmdImpl = _cmd2Impl.get(cmd);
						cmdImpl.run();
					}
					else if (cmd.startsWith("+") || (cmd.startsWith("-")))
					{
						if (!_isStartingCall)
						{
							System.out.println("All calls have already been committed, can not change CAPS.");
							continue;
						}
						int delta = Integer.parseInt(cmd.substring(1).trim());
						int targetCAPS = cmd.startsWith("+") ? _currentCAPS + delta : _currentCAPS - delta;
						if (targetCAPS <= 0)
						{
							System.out.println("Can not change CAPS to " + targetCAPS);
							continue;
						}
						setCAPS(targetCAPS);
						System.out.println("CAPS is set to " + _currentCAPS);
					}
					else
					{
						System.out.println("======== Command HELP ========");
						System.out.println("start	- Start Call");
						System.out.println("[Enter]- report basic data");
						System.out.println("+[DeltaCallPerSecond]- Increase call rate [DeltaCallPerSecond] ");
						System.out.println("-[DeltaCallPerSecond]- Decrease call rate [DeltaCallPerSecond] ");
						System.out.println("stat [all] - List real time statistical data");
						System.out.println("exit	- Exit");
						System.out.println("convert - Convert all statistical data files to CSV format.");
						System.out.println("======== Extended Command =======");
						for (String c : _cmd2Impl.keySet())
							System.out.println(c + "-" + _cmd2Desc.get(c));
					}
				}
				catch (Throwable e)
				{
					System.err.println("ERROR:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		catch (InterruptedIOException e)
		{
			exit();
		}

	}

	private void convertStatistic()
	{
//		File dir = new File("logs");
		String logFolder = _appProperties.getProperty("ClientApp.LogFolder");
		if(logFolder == null || logFolder.isEmpty()){
			logFolder = "logs";
		}
		File dir = new File(logFolder);
		System.out.println("Scanning directory " + dir.getAbsolutePath() + "...");
		for (File file : dir.listFiles())
		{
			if (!file.getName().endsWith(".ser")) continue;
			File fOut = new File(file.getParentFile(), file.getName() + ".csv");
			// COMMENT off on 20161019
//			if (fOut.exists())
//			{
//				System.out.println("Skipped existing CSV file " + fOut.getName());
//				continue;
//			}
			if (fOut.exists()){
				System.out.println("Existing CSV file " + fOut.getName() + ", rewrite this file....");
				fOut.delete();
			}
			StatisticConverter converter = new StatisticConverter(file, fOut);
			try
			{
				converter.convert();
				System.out.println("Converted " + file.getName() + " to " + fOut.getName() + ".");
			}
			catch (Exception e)
			{
				System.out.println("Converted " + file.getName() + " error:" + e.getMessage() + ", skipped.");
			}

		}
	}

	private Map<String, Runnable> _cmd2Impl = new HashMap<String, Runnable>();
	private Map<String, String> _cmd2Desc = new HashMap<String, String>();

	public void registerCommand(String cmd, String cmdDesc, Runnable cmdImpl)
	{
		_cmd2Impl.put(cmd, cmdImpl);
		_cmd2Desc.put(cmd, cmdDesc);
	}

	static Set<String> REPORT_MESSAGE_NAME_LIST = new HashSet<String>();

	static
	{
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.TRANSACTION_BEGIN);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.TRANSACTION_UPDATE);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.TRANSACTION_END);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.MSG_DELETE);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.MSG_INSERT);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.MSG_UPDATE);
		REPORT_MESSAGE_NAME_LIST.add(StatisticCall.MSG_QUERY);
	
	}

	long _rptLastTime, _rptLastStartedCall, _rptLastSuccessCall, _rptLastTransactionRequest, _rptLastTransactionTotalProcessTime;

	private void reportStatus()
	{
		long now = System.currentTimeMillis();
		System.out.println("============" + _name + "===============");
		System.out.println("Committed call " + _callBegun + " of " + _callCount);
		System.out.println("Active call " + _statisticCall.getActiveSession());
		System.out.println("Active Threads: " + Thread.activeCount());
		System.out.println("Task Executor: " + _taskExecutor.toString());
		//System.out.println("Session pool idle count : " + _dataGenerator.getSessionPoolIdleCount());
		//
		System.out.println("Pending (Prepared and not begin ) calls: " + CallExecutor.getPendingCallCount());
		if (_statisticCall.getStartCallCounter() > 0)
			System.out.println("Pending(Prepare-Begin) time (ms): Average " + (CallExecutor.getStatPendingTimeTotal() / _statisticCall.getStartCallCounter()) + ", Max:"
					+ CallExecutor.getStatPendingTimeMax());
		System.out.println("Delay(Update/End) time (ms): Average " + CallExecutor.getStatUEDelayTimeAverage() + ", Max:" + CallExecutor.getStatUEDelayTimeMax());
		//
		if (_enableCallRateControl)
			System.out.println("Target Call per second: " + _currentCAPS);
		else
			System.out.println("Automatic call rate control with MaxPendingCallCount " + _maxPendingCallCount);
		//
		if (_rptLastTime > 0)
		{
			long duration = (now - _rptLastTime);
			if (duration > 0)
			{
				System.out.println("============From " + new Date(_rptLastTime) + " to now (" + new Date(now) + ") ===============");
				long calls = _statisticCall.getStartCallCounter();
				long tx = _statisticCall.getTransactionResponseCount(REPORT_MESSAGE_NAME_LIST);
				long txProcessTime = _statisticCall.getTransactionProcessTime(REPORT_MESSAGE_NAME_LIST);

				long deltaStartCall = calls - _rptLastStartedCall;
				_rptLastStartedCall = calls;
				//
				long deltaTx = tx - _rptLastTransactionRequest;
				_rptLastTransactionRequest = tx;
				//
				long deltaTxProcessTime = txProcessTime - _rptLastTransactionTotalProcessTime;
				_rptLastTransactionTotalProcessTime = txProcessTime;

				calls = _statisticCall.getSuccessCallCounter();
				long deltaSuccCall = calls - _rptLastSuccessCall;
				_rptLastSuccessCall = calls;
				//
				System.out.println("Rough Started Call per second: " + (1000 * deltaStartCall / duration));
				System.out.println("Rough Success Call per second: " + (1000 * deltaSuccCall / duration));
				System.out.println("Rough Transaction(Except Prepare) per second: " + (1000 * deltaTx / duration));
				if (deltaTx > 0)
					System.out.println("Rough Transaction(Except Prepare) Latency(ms): " + (deltaTxProcessTime / deltaTx));
			}
		}
		else
		{
			_rptLastStartedCall = _statisticCall.getStartCallCounter();
			_rptLastSuccessCall = _statisticCall.getSuccessCallCounter();
			_rptLastTransactionRequest = _statisticCall.getTransactionResponseCount(REPORT_MESSAGE_NAME_LIST);
			_rptLastTransactionTotalProcessTime = _statisticCall.getTransactionProcessTime(REPORT_MESSAGE_NAME_LIST);
		}
		_rptLastTime = now;
		//
		long duration = (now - _statisticCall._timestampBegin);
		if (duration > 0)
		{
			System.out.println("============From Begin time " + new Date(_statisticCall._timestampBegin) + " to now(" + new Date(now) + ") ===============");
			System.out.println("Rough Started Call per second: " + (1000 * _statisticCall.getStartCallCounter() / duration));
			System.out.println("Rough Success Call per second: " + (1000 * _statisticCall.getSuccessCallCounter() / duration));
			System.out.println("Rough Transaction(Except Prepare) per second: " + (1000 * _statisticCall.getTransactionResponseCount(REPORT_MESSAGE_NAME_LIST) / duration));
			long tx = _statisticCall.getTransactionResponseCount(REPORT_MESSAGE_NAME_LIST);
			long txProcessTime = _statisticCall.getTransactionProcessTime(REPORT_MESSAGE_NAME_LIST);
			if (tx > 0) System.out.println("Rough Transaction(Except Prepare) Latency(ms): " + (txProcessTime / tx));
		}

	}

/*
	public String generateSessionID()
	{
		return _dataGenerator.generateSessionID();
	}

	public SNAP$OcsSession generateSession(String sessionID, String deviceIdentifier)
	{
		return _dataGenerator.generateSession(sessionID, Long.valueOf(deviceIdentifier));
	}

	public SNAPDataGenerator.SprDataHolder generateOneSPRData(long deviceIdentifier)
	{
		return _dataGenerator.generateOneSubscriber(deviceIdentifier);
	}
*/

	public SNAPDataGenerator getDataGenerator()
	{
		return _dataGenerator;
	}

	public String getNextIdentifierInRandom()
	{
		return _sectionIdentifer.selectRandom();
	}
	// TODO delete
//	public void insertSPR2JDBC(Connection conn, SNAPDataGenerator.SprDataHolder sdh) throws SQLException
//	{
//		_dataGenerator.insertSPR2JDBC(conn, sdh);
//	}

	private Runnable _callbackBegin, _callbackEnd;

	public void registerServiceNotification(Runnable callbackBegin, Runnable callbackEnd)
	{
		_callbackBegin = callbackBegin;
		_callbackEnd = callbackEnd;
	}
	private int _autoExitDelayWhileCompleted =10;
	void notifyCallCompleted()
	{
		if (_callbackEnd != null) _callbackEnd.run();
		if(_autoExitDelayWhileCompleted<=0)
		{
			System.out.println("Service is completed, But auto exit is disabled. Input exit to exit.");
			return;
		}
		if (_consoleThread != null) _consoleThread.interrupt();
		System.out.println("Service is completed, Auto exit after "+ _autoExitDelayWhileCompleted +" seconds.");
		try
		{
			Thread.sleep(_autoExitDelayWhileCompleted *1000);
		}
		catch (InterruptedException e)
		{
		}
		exit();
	}

	public void submitTask(Callable<?> task)
	{
		_taskExecutor.submitImmediate(task);//_poolExecutor.submit(task);//_poolSessionUpdate.submit(task);
	}


	private class Section
	{
		Section(String section)
		{
			StringTokenizer st = new StringTokenizer(section, "-");
			String begin = st.nextToken();
			String end = st.nextToken();
			if (begin.length() != end.length())
				throw new IllegalArgumentException("Segment number Must have same length:" + begin + "=>" + end);
			int i = 0;
			while ((begin.charAt(i) == end.charAt(i)) && (i < begin.length()))
				i++;
			_prefix = begin.substring(0, i);
			_min = Long.parseLong(begin.substring(i));
			_max = Long.parseLong(end.substring(i));
			if (_max < _min)
			{
				long a = _max;
				_max = _min;
				_min = a;
			}
			if ((_max - _min) > Integer.MAX_VALUE)
				throw new IllegalArgumentException("Too many numbers in segment:" + begin + "=>" + end);
		}

		synchronized String selectNext()
		{
			if (_idxCurValue > (_max - _min) || (_idxCurValue < 0)) _idxCurValue = 0;
			StringBuffer num2 = new StringBuffer(String.valueOf(_min + _idxCurValue));
			int numlen = String.valueOf(_max).length();
			while (num2.length() < numlen) num2.insert(0, '0');
			num2.insert(0, _prefix);
			String result = num2.toString();
			_idxCurValue++;
			return result;
		}

		synchronized String selectRandom()
		{
			int i = _rand.nextInt((int) (_max - _min));
			StringBuffer num2 = new StringBuffer(String.valueOf(_min + i));
			int numlen = String.valueOf(_max).length();
			while (num2.length() < numlen) num2.insert(0, '0');
			num2.insert(0, _prefix);
			String result = num2.toString();
			_idxCurValue++;
			return result;
		}

		private String _prefix;// NOT include variable
		private long _min, _max, _idxCurValue;
	}

	public int getCallCount()
	{
		return _callCount;
	}

	@Override
	public String toString()
	{
		return "CallService{" + _name + '}';
	}

	private SNAPDataGenerator _dataGenerator;
	private int _currentCAPS;

	private void setCAPS(int targetCAPS)
	{
		_currentCAPS = targetCAPS;
		_enableCallRateControl = true;
        _maxCallPerSlot = _timeSlotMs * targetCAPS / (1000 * 1000 * 1000L);
		if (_maxCallPerSlot < 1)
		{
            _timeSlotMs = 1 * 1000 * 1000 * 1000L; // nanosecond = 1 second
			_maxCallPerSlot = targetCAPS;
		}
		_logger.info("Set CallPerSecond to " + targetCAPS + " for service " + _name);
	}
}
