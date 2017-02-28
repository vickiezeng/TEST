package com.hp.snap.evaluation.imdb.business.cases.couchbase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hp.snap.evaluation.imdb.business.common.CallExecutorAsync;

public class AsyncExecutor {

	static CallExecutorAsync callExecutor;
	
	static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	
	public static Executor getExecutor() {
		return pool;
	}
	
	public static void setExecutor(CallExecutorAsync callExecutor) {
		AsyncExecutor.callExecutor = callExecutor;
	}
	
}
