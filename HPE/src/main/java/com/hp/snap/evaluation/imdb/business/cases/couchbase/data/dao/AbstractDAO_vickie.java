package com.hp.snap.evaluation.imdb.business.cases.couchbase.data.dao;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.core.BackpressureException;
import com.couchbase.client.core.time.Delay;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.JsonLongDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.transcoder.JsonTranscoder;
import com.couchbase.client.java.util.retry.RetryBuilder;
import com.hp.snap.evaluation.imdb.business.cases.ByteJsonDocument;
import com.hp.snap.evaluation.imdb.business.cases.JacksonConverter;
import com.hp.snap.evaluation.imdb.business.cases.JsonConverter;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.AsyncResponse;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.ErrorResponse;
import com.hp.snap.evaluation.imdb.business.cases.couchbase.data.PrimaryKey;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class AbstractDAO_vickie<T extends PrimaryKey> {
	
	
	private final static ErrorResponse ErrorResponse = new ErrorResponse();
	
	protected final JsonConverter converter = new JacksonConverter();
	protected final JsonTranscoder transcoder = new JsonTranscoder();
	
	protected Bucket bucket;
	protected PersistTo persistTo;
	protected ReplicateTo replicateTo;

	private Subscription subscribe;
	
	public AbstractDAO_vickie(Bucket bucket){
		this.bucket = bucket;
	}
	
	 public void insert(final T instance, final AsyncResponse run) {
	    	if(instance.getId() == null || instance.getId().trim().length() == 0) {
				String id = getNextId(instance.getClass(), 1, 100);
				instance.setId(id);
			}
	    	ByteJsonDocument docIn = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
//	    	CountDownLatch latch = new CountDownLatch(1);    	
	    	Observable<ByteJsonDocument> ov = null;
	    	if (persistTo != null && replicateTo != null) {
	    		ov = bucket.async().insert(docIn, persistTo, replicateTo).retryWhen(RetryBuilder
	    			    .anyOf(BackpressureException.class).max(3)
	    			    .delay(getDelaySetting()).build());
	    	} else {
	    		ov = bucket.async().insert(docIn).retryWhen(RetryBuilder
	    			    .anyOf(BackpressureException.class).max(3)
	    			    .delay(getDelaySetting()).build());;
	    	}
	    	ov.subscribe(new Action1<ByteJsonDocument>() {
				@Override
				public void call(final ByteJsonDocument update) {
//					latch.countDown(); 
					if(null != run){
						//no need converting t to object again due to update/delete.
							
				    	run.future = instance;
				    	run.run();	
				    	
					}
//					latch.countDown();
				}
			});
	    	
//	    	ov.subscribe(new UpdateAction(0, 1, instance, run,latch));
//	    	try {
//				latch.await();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }

	private static Delay getDelaySetting() {
		return Delay.exponential(TimeUnit.MILLISECONDS, 28, 3, 1, 2);
	}
    
    public void insert(final T instance) {
    	if(instance.getId() == null || instance.getId().trim().length() == 0) {
			String id = getNextId(instance.getClass(), 1, 100);
			instance.setId(id);
		}
    	ByteJsonDocument docIn = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
    	if (persistTo != null && replicateTo != null) {
    		bucket.insert(docIn, persistTo, replicateTo);
    	} else {
    		bucket.insert(docIn);
    	}
    }

    public void update(final T instance, final AsyncResponse run) {
    	ByteJsonDocument docIn = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
//    	CountDownLatch latch = new CountDownLatch(1);
    	Observable<ByteJsonDocument> ov = null;
    	if (persistTo != null && replicateTo != null) {
    		ov = bucket.async().upsert(docIn, persistTo, replicateTo).retryWhen(RetryBuilder
    			    .anyOf(BackpressureException.class).max(3)
    			    .delay(getDelaySetting()).build());;
    	} else {
    		ov = bucket.async().upsert(docIn).retryWhen(RetryBuilder
    			    .anyOf(BackpressureException.class).max(3)
    			    .delay(getDelaySetting()).build());
    	}
    	ov.subscribe(new Action1<ByteJsonDocument>() {
			@Override
			public void call(final ByteJsonDocument update) {
//				latch.countDown();
				if(null != run){
					//no need converting t to object again due to update/delete.
					
			    	run.future = instance;
			    	run.run();	
			    	
			    	
				}
//				latch.countDown();
			}
		});
//    	ov.subscribe(new UpdateAction(0, 2, instance, run,latch));
//    	try {
//			latch.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    public void update(final T instance) {
    	ByteJsonDocument docIn = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
    	if (persistTo != null && replicateTo != null) {
    		bucket.upsert(docIn, persistTo, replicateTo);
    	} else {
    		bucket.upsert(docIn);
    	}
    }
    
    /**
     * delete by primary key
     * @param session
     * @param key
     */
    public void delete(final T instance, final AsyncResponse run)  {
    	ByteJsonDocument doc = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
//    	CountDownLatch latch = new CountDownLatch(1);
    	Observable<ByteJsonDocument> ov = null;
    	if (persistTo != null && replicateTo != null) {
    		ov = bucket.async().remove(doc, persistTo, replicateTo).retryWhen(RetryBuilder
    			    .anyOf(BackpressureException.class).max(3)
    			    .delay(getDelaySetting()).build());;
    	} else {
    		ov = bucket.async().remove(doc).retryWhen(RetryBuilder
    			    .anyOf(BackpressureException.class).max(3)
    			    .delay(getDelaySetting()).build());;
    	}
    	ov.subscribe(new Action1<ByteJsonDocument>() {
			@Override
			public void call(final ByteJsonDocument update) {
//				latch.countDown();
				if(null != run){
					//no need converting t to object again due to update/delete.
			    	run.future = instance;
			    	run.run();    	
				}
//				latch.countDown();
			}
		});
//    	ov.subscribe(new UpdateAction(0, 3, instance, run,latch));
//    	try {
//			latch.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    
    
    public void delete(final T instance) {
    	ByteJsonDocument doc = ByteJsonDocument.create(instance.getId(), converter.toBytes(instance));
    	if (persistTo != null && replicateTo != null) {
    		bucket.remove(doc, persistTo, replicateTo);
    	} else {
    		bucket.remove(doc);
    	}
    }
    
    /**
     * get by primary key
     * @param id
     */
    public void findByPrimaryKey(String id, final Class<? extends PrimaryKey> mapping, final AsyncResponse<T> run)  {
//    	CountDownLatch latch = new CountDownLatch(1);
    	bucket.async().get(id, ByteJsonDocument.class).retryWhen(RetryBuilder
			    .anyOf(BackpressureException.class).max(3)
			    .delay(getDelaySetting()).build()).map(new Func1<ByteJsonDocument, PrimaryKey>() {
		            @Override
		            public PrimaryKey call(ByteJsonDocument i) {
		            	PrimaryKey result = fromByteJsonDocument(i, mapping);
						result.setCas(i.cas());
		            	return result;
		            }
		        }).subscribe(new Action1<PrimaryKey>() {
					@Override
					public void call(final PrimaryKey result) {
//						latch.countDown();
						if(null != run){	
					    	run.future = result;
					    	run.run();
						}
					    	
					}
				});	        
		        
//		        .subscribe(new FindPkeyAction(run,latch));
//    	try {
//			latch.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    
    public T findByPrimaryKey(String id, final Class<T> mapping) {
    	ByteJsonDocument doc = bucket.get(id, ByteJsonDocument.class);
    	T result = converter.fromBytes(doc.content(), mapping);
		result.setCas(doc.cas());
    	return result;
    }
    
    
    /**
	 * Converts a JsonDocument into an object of the specified type
	 * 
	 * @param doc JsonDocument to be converted
	 * @param type Class<T> that represents the type of this or a parent class
	 * @return Reference to an object of the specified type
	 */
    public static <T extends PrimaryKey> T fromJsonDocument(JsonConverter converter, JsonDocument doc, Class<T> type) {
		if (doc == null) {
			throw new IllegalArgumentException("document is null");
		}
		JsonObject content = doc.content();
		if (content == null) {
			throw new IllegalStateException("document has no content");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		T result = converter.fromJson(content.toString(), type);
		result.setCas(doc.cas());
		return result;
	}
	
    public <T extends PrimaryKey> T fromJsonDocument(JsonDocument doc, Class<T> type) {
		if (doc == null) {
			throw new IllegalArgumentException("document is null");
		}
		JsonObject content = doc.content();
		if (content == null) {
			throw new IllegalStateException("document has no content");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		T result = converter.fromJson(content.toString(), type);
		result.setCas(doc.cas());
		return result;
	}
    
	public <T extends PrimaryKey> T fromRawJsonDocument(RawJsonDocument doc, Class<T> type) {
		if (doc == null) {
			throw new IllegalArgumentException("document is null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		T result = converter.fromJson(doc.content(), type);
		result.setCas(doc.cas());
		return result;
	}
	
	public <T extends PrimaryKey> T fromByteJsonDocument(ByteJsonDocument doc, Class<T> type) {
		if (doc == null) {
			throw new IllegalArgumentException("document is null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		T result = converter.fromBytes(doc.content(), type);
		result.setCas(doc.cas());
		return result;
	}

	/**
	 * Converts an object to a JsonDocument
	 * 
	 * @param source Object to be converted
	 * @return JsonDocument that represents the specified object
	 */
	public <T extends PrimaryKey> JsonDocument toJsonDocument(T source) {
		if (source == null) {
			throw new IllegalArgumentException("entity is null");
		}
		String id = source.getId();
		if (id == null) {
			throw new IllegalStateException("entity ID is null");
		}
		JsonObject content;
		try {
			content = transcoder.stringToJsonObject(converter.toJson(source));
			return JsonDocument.create(id, content, source.getCas());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
    
	public static <T extends PrimaryKey> JsonDocument toJsonDocument(JsonTranscoder transcoder, JsonConverter converter, T source) {
		if (source == null) {
			throw new IllegalArgumentException("entity is null");
		}
		String id = source.getId();
		if (id == null) {
			throw new IllegalStateException("entity ID is null");
		}
		JsonObject content;
		try {
			content = transcoder.stringToJsonObject(converter.toJson(source));
			return JsonDocument.create(id, content, source.getCas());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
    
	public <T extends PrimaryKey> RawJsonDocument toRawJsonDocument(T source) {
		if (source == null) {
			throw new IllegalArgumentException("entity is null");
		}
		String id = source.getId();
		if (id == null) {
			throw new IllegalStateException("entity ID is null");
		}
		JsonObject content;
		try {
			RawJsonDocument docIn = RawJsonDocument.create(source.getId(), converter.toJson(source));
			return docIn;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Generates an ID using the Couchbase counter feature.
	 * 
	 * @param type Class<T> that represents the type of the entity
	 * @param init Initial value of the counter
	 * @param incr Amount to increment the counter value each time
	 * @return Next value of the counter
	 */
	protected <T> String getNextId(Class<T> type, long incr, 
		long init) {
		String name = type.getSimpleName().toLowerCase();
		JsonLongDocument doc = 
			bucket.counter("counter::" + name, incr, init);
		return name + "::" + doc.content().toString();
	}
}
