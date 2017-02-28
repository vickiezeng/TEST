/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common
** Date: 7/30/12				Time: 10:20 AM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * TODO description of this Class
 */
public class STFThreadPoolExecutor
{

	private int _threadPerScheduler;
	private int _threadImmediate;
    private Random random = new Random();

	public STFThreadPoolExecutor(int threadCount)
	{
		_threadImmediate = threadCount;
		ThreadFactory tf=new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread t=new Thread(r);
				t.setName("Pool-Immed-"+t.getId());
				return t;
			}
		};
		_poolExecutor = Executors.newFixedThreadPool(threadCount, tf);
		_logger.info("Create  STFThreadPoolExecutor with " + threadCount + " threads to execute immediate tasks.");
		//
		_threadPerScheduler = threadCount / 20;
		if (_threadPerScheduler < 2) _threadPerScheduler = 2;
		_logger.info("Create STFThreadPoolExecutor scheduled executor has " + _threadPerScheduler + " threads.");
		_scheduleExecutors = new ScheduledThreadPoolExecutor(_threadPerScheduler, new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread t=new Thread(r);
				t.setName("Pool-Sched-"+t.getId());
				return t;
			}
		});
		//
	}

	private class ScheduleTaskWrapper implements Callable<Object>
	{
		ScheduleTaskWrapper(CallExecutor ce)
		{
			_ce = ce;
		}

		CallExecutor _ce;

		public Object call() throws Exception
		{
			long now=System.currentTimeMillis();
			long delay=0;
			if(_ce.getLastNextTime()>0)
			{
				delay=now-_ce.getLastNextTime();
				_delaySchedulerCount++;
				_delaySchedulerTotal += delay;
				if(delay>_delaySchedulerMax) _delaySchedulerMax=delay;
			}
			submitImmediate(_ce);//_poolExecutor.submit(_ce);
			return _ce;
		}
	}

	public void schedule(CallExecutor cs, long delayMs)
	{
        long delayNs = delayMs * 1000000 + (long) (random.nextDouble() * 1000000);
		_scheduleExecutors.schedule(new ScheduleTaskWrapper(cs), delayNs, TimeUnit.NANOSECONDS);
	}


	public void submitImmediate(Callable<?> cs)
	{
		_poolExecutorsSubmitCounter++;
		_poolExecutor.submit(cs);
	}


	public void shutdown()
	{
		_scheduleExecutors.shutdown();
		_poolExecutor.shutdown();
	}

	private ScheduledThreadPoolExecutor _scheduleExecutors;
	private ExecutorService _poolExecutor;
	private int _poolExecutorsSubmitCounter;
	private Logger _logger = Logger.getLogger(getClass().getName());

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append("{Scheduler Queue:");
		sb.append(getSchedulerQueueSize()).append(";").append("Delay:").append(getAverageScheduleDelay()).append(",Max:").append(_delaySchedulerMax).append(".");
		sb.append("ImmediateThreads:").append(_threadImmediate).append(";Queue:").append(getImmediateQueueSize());
		sb.append("}.");

		return sb.toString();
	}

	public int getSchedulerQueueSize() {return _scheduleExecutors.getQueue().size();}

	public int getImmediateQueueSize()
	{
		return ((ThreadPoolExecutor) _poolExecutor).getQueue().size();
	}
	private long _delaySchedulerMax, _delaySchedulerTotal, _delaySchedulerCount;
	public long getAverageScheduleDelay()
	{
		if(_delaySchedulerCount>0)  return  _delaySchedulerTotal/_delaySchedulerCount;
		return -1;
	}
}
