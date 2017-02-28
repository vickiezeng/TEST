package com.hp.snap.evaluation.imdb.business.cases;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.query.PrepareStatement;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import com.hp.snap.evaluation.imdb.business.common.CallService;
//import com.hp.usage.datastruct.transform.json.JsonIntrospectionSerializer;
//import com.hp.usage.datastruct.transform.json.JsonSerializer;
//import com.hp.usage.nme.NME;
//import com.hp.usage.nme.NMEFactory;
//import com.hp.usage.nme.NMEImplRegistrarHelper;
//import com.hp.usage.nme.NMEManager;
//import com.hp.usage.nme.NMESchema;
//import com.hp.usage.nme.schemaloader.NMESchemaLoader;

/**
 * 
 * FieldExtracted=UserName,LoginID,INVALID
FieldExtracted=IPAddress,SrcIP
JDBCDataSourceConnectorName=DataSourceConnector
QueryConditionColumns=IPAddress,Varchar
QueryString=select %FIELDS from TEST_USER where IPAddress=%SrcIP
ResultAttribute=Result
ResultIndexAttribute=ResultIndex
UseDefaults=true
FailedConnectionAttemptsLimit=3
 * 
 * 
 * Query Optimization Using Prepared (Optimized) Statements
 * When a N1QL query string is sent to the server, the server will inspect the string and parse it, 
 * planning which indexes to query. Once this is done, it generates a query plan. The computation 
 * for the plan adds some additional processing time and overhead for the query.
 * Often-used queries can be prepared so that its plan is generated only once. Subsequent queries 
 * using the same query string will use the pre-generated plan instead, saving on the overhead and 
 * processing of the plan each time.
 * 
 * You can indicate to the SDK that a given query should be optimized in the above fashion. When an
 * SDK sees that a query should be optimized, it will internally prepare the statement and store the
 * plan in an internal cache. When issuing the query again, the SDK will check to see if a plan 
 * exists in its cache, and will send the plan to the server.
 * 
 * To indicate that an SDK should optimize a query, the adhoc parameter should be set to false. When
 * a query is not ad-hoc, the SDK will fetch the plan (if it does not already have it). Do not turn
 * off the adhoc flag for each query since only a finite number of query plans (currently 5000) can
 * be stored in the SDK.
 * 
 * 
 * http://query.pub.couchbase.com/tutorial/
 * 
 * @author wushaol
 *
 */
public class C7N1Query {
	
	public static final String XSD_FILE = "SerializerGeneratorTest.xsd";
	
	private ClusterSessionPool sessionPool;
	
	private PrepareStatement preparedStatement;
	
	private N1qlParams n1qlSetting;
	
	private int id = 0;
	
	public C7N1Query() {
		sessionPool = new ClusterSessionPool();
	}

//    static {
//        NMEImplRegistrarHelper.registerDefault();
//        loadSchema();
//    }
//
//    public static void loadSchema() {
//        try {
//        	NMESchema nmeSchema = NMEManager.getNMESchema();
//        	new NMESchemaLoader().loadXSDSchema(getResource(XSD_FILE), nmeSchema);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//    }

    protected static File getResource(String name) throws URISyntaxException {
        URL url = C7N1Query.class.getClassLoader().getResource(name);
        File file = new File(url.toURI());
        return file;
    }
	
	
	public static void main(String[] args) throws Exception {
		CallService _service = new CallService(C4J.class.getSimpleName());
        _service.configure();
		
		C7N1Query query = new C7N1Query();
		query.configure(args);
		query.run();
	}
	
	/**
	 *  
	 * @param sql
	 * @throws Exception
	 */
	public void configure(String[] args) throws Exception {
		String sql = "SELECT text, decimal, complex FROM doquery where text is NOT NULL"; // 
		preparedStatement = PrepareStatement.prepare(sql);
		n1qlSetting = N1qlParams.build();
		n1qlSetting.adhoc(false);
		n1qlSetting.consistency(ScanConsistency.NOT_BOUNDED); //configurable
		n1qlSetting.serverSideTimeout(5000, TimeUnit.MILLISECONDS); //configurable
		buildInput();
	}
	
	public void run() {
		Bucket bucket = sessionPool.getSession("doquery");
		
//		NMESchema nmeSchema = NMEManager.getNMESchema();
//		NMEFactory nmeFactory = NMEManager.getNMEFactory();
//        JsonSerializer jsonSerializer = new JsonIntrospectionSerializer("NME_JSerializer:RootType");
//
//        NME sourceNme = new NMEDataGenerator(nmeFactory, 5, 2).nextNME(nmeSchema.getNMEType("NME_JSerializer:RootType"));
//        String json = jsonSerializer.write(sourceNme);
//        System.out.println("input NME: " + json);
//        String key = ""+(++id);
//        RawJsonDocument docIn = RawJsonDocument.create(key, json);
//        bucket.upsert(docIn);
//		
//		JsonObject namedParams = JsonObject.create();
//		namedParams.put("a", true);
//		ParameterizedN1qlQuery query = N1qlQuery.parameterized(preparedStatement, namedParams, n1qlSetting); 
//		N1qlQueryResult queryResult = bucket.query(query);
//		if (queryResult.finalSuccess()) {
//			List<N1qlQueryRow> rows = queryResult.allRows();
//			System.out.println("Query success! extracting rows("+rows.size()+")...");
//			buildOutput(jsonSerializer, rows);
//		} else {
//			System.out.println("Error query!");
//			List<JsonObject> errors = queryResult.errors();
//			for (JsonObject j : errors) {
//				System.out.println("error row: " + j.toString());
//			}
//		}
	}
	
	private void buildInput() {
		//TODO
	}
	
//	private void buildOutput(JsonSerializer jsonSerializer, List<N1qlQueryRow> rows) {
//		for (N1qlQueryRow row : rows) {
//			if (row.value().getObject("doquery") != null) {
//				System.out.println("queried Json row: " + row.value().getObject("doquery").toString());
//				NME nme = jsonSerializer.read(row.value().getObject("doquery").toString());
//				System.out.println("converted NME row: " + nme.toString());
//			} else {
//				System.out.println("queried Json row: " + row.value().toString());
//				NME nme = jsonSerializer.read(row.value().toString());
//				System.out.println("converted NME row: " + nme.toString());
//			}
//		}
//		
//	}
}
