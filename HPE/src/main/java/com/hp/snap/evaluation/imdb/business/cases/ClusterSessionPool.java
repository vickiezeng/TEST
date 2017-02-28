package com.hp.snap.evaluation.imdb.business.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.couchbase.client.core.event.consumers.LoggingConsumer;
import com.couchbase.client.core.logging.CouchbaseLogLevel;
import com.couchbase.client.core.metrics.DefaultLatencyMetricsCollectorConfig;
import com.couchbase.client.core.metrics.DefaultMetricsCollectorConfig;
import com.couchbase.client.core.time.Delay;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment.Builder;
import com.couchbase.client.java.transcoder.Transcoder;
import com.hp.snap.evaluation.imdb.business.common.CallService;

public class ClusterSessionPool {
	private static Logger logger = Logger.getLogger(ClusterSessionPool.class
			.getName());

	private final CouchbaseCluster cluster;
	
	private final Map<String, Bucket> buckets = new HashMap<String, Bucket>();
	
	private final  List<Transcoder<? extends Document, ?>> transcoders = new ArrayList<Transcoder<? extends Document, ?>>();
	
	public ClusterSessionPool() {
        transcoders.add(new ByteJsonTranscoder());
		// Properties props = new Properties();
		Properties config = CallService.getInstance().getConfig();
		String clusterContactPoints = config
				.getProperty(CallService.PROP_CLUSTER_CONTACT_POINTS);
		if (null == clusterContactPoints || clusterContactPoints.isEmpty()) {
			logger.info(CallService.PROP_CLUSTER_CONTACT_POINTS
					+ " can be null!");
		}
		Builder b = DefaultCouchbaseEnvironment.builder();
		b.requestBufferSize(1024*1024).responseBufferSize(1024*1024);
		b.callbacksOnIoPool(true);
		if (config.getProperty("cluster.Delay.fix") != null) {
			long growby = Long.parseLong(config.getProperty("cluster.Delay.growby"));
			b.observeIntervalDelay(Delay.fixed(growby, TimeUnit.MICROSECONDS));
		} else if (config.getProperty("cluster.Delay.linear") != null) {
			long upper = Long.parseLong(config.getProperty("cluster.Delay.upper"));
			long lower = Long.parseLong(config.getProperty("cluster.Delay.lower"));
			long growby = Long.parseLong(config.getProperty("cluster.Delay.growby"));
			b.observeIntervalDelay(Delay.linear(TimeUnit.MICROSECONDS, upper, lower, growby));
		} else if (config.getProperty("cluster.Delay.expo") != null) {
			long upper = Long.parseLong(config.getProperty("cluster.Delay.upper"));
			long lower = Long.parseLong(config.getProperty("cluster.Delay.lower"));
			b.observeIntervalDelay(Delay.exponential(TimeUnit.MICROSECONDS, upper, lower, 1, 2));
		}
		if (config.getProperty("cluster.kvEndpoints") != null) {
			b.kvEndpoints(Integer.parseInt(config.getProperty("cluster.kvEndpoints")));
		}
		if (config.getProperty("cluster.enable.metrics") != null) {
			b.defaultMetricsLoggingConsumer(true, CouchbaseLogLevel.INFO, LoggingConsumer.OutputFormat.JSON_PRETTY)
				.runtimeMetricsCollectorConfig(DefaultMetricsCollectorConfig.create(2, TimeUnit.MINUTES))
				.networkLatencyMetricsCollectorConfig(DefaultLatencyMetricsCollectorConfig.create(2, TimeUnit.MINUTES));
		}
		//rx.plugins.RxJavaSchedulersHook
		//rx.schedulers.Schedulers;
		//b.scheduler(CallService.getInstance().getTaskExecutor().getScheduleExecutors());
		//b.computationPoolSize(Runtime.getRuntime().availableProcessors() * 2);
		DefaultCouchbaseEnvironment env = b.build();
		logger.info("Configured cluster environment: " + env.toString());
		String[] clusterContactPointArray = clusterContactPoints.split(",");
		logger.info("Connecting cluster with port " + clusterContactPoints);
		cluster = CouchbaseCluster.create(env, clusterContactPointArray);
		logger.info("Created connection to cluster with points " + clusterContactPoints);
	}

	public synchronized Bucket getSession(String bucketName) {
		if (buckets.containsKey(bucketName)) {
			return buckets.get(bucketName);
		}
		Bucket bucket = cluster.openBucket(bucketName, "", transcoders);
		buckets.put(bucketName, bucket);
		return bucket;
	}

	public void shutdown() {
		Collection<Bucket> values = buckets.values();
		for (Bucket b : values) {
			try {
				b.close();
			} catch (Exception e) {
			}
		}
		cluster.disconnect();
	}

}
