package db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ListIterator;

import org.json.JSONObject;

import request.WordRelevance;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DatabaseManager {

	// MongoDB values
	private static String DB_NAME = "RI-DB";
	private static String COLLECTION_NAME = "words";
	private static DBCollection COLLECTION ;

	public DatabaseManager() {
		MongoClient mongo;
		try {
			mongo = new MongoClient( "localhost" , 27017 );
			DB db = mongo.getDB(DB_NAME);
			COLLECTION = db.getCollection(COLLECTION_NAME);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
	}

	public void insertDBEntry(JSONObject obj) {
		COLLECTION.insert(((BasicDBObject)com.mongodb.util.JSON.parse(obj.toString())));
	}

	/**
	 * Retrieve a JSON in the db containing a word and the associated documents
	 * @param word to retrieve
	 * @return
	 */
	public DBObject getDBEntry(String word) {
		BasicDBObject query = new BasicDBObject("word", word);

		DBCursor cursor = COLLECTION.find(query);
		DBObject obj = null;

		try {
			while(cursor.hasNext())
				obj = cursor.next();
		} 
		finally {
			cursor.close();
		}

		return obj;
	}

	/**
	 * Retrieve information for each document in the db entry
	 * @param obj
	 * @return
	 */
	public ArrayList<WordRelevance> getWordRelevance(String word) {
		ArrayList<WordRelevance> infoList = new ArrayList<WordRelevance>();
		DBObject dbObj = getDBEntry(word);

		ListIterator<Object> appearances = ((BasicDBList) dbObj.get("appearances")).listIterator();

		while(appearances.hasNext()){
			WordRelevance info = new WordRelevance();
			DBObject nextItem = (DBObject) appearances.next();

			info.setName(nextItem.get("name").toString());
			info.setOccurrences((Integer) nextItem.get("tf"));

			infoList.add(info);
		}

		return infoList;
	}

	public boolean isInDB(String word) {
		BasicDBObject query = new BasicDBObject("word", word);

		if (COLLECTION.findOne(query) == null)
			return false;
		else
			return true;
	}

	public int wordFrequencyInCorpus(String word) {
		BasicDBObject query = new BasicDBObject("word", word);

		DBObject wordInfo = COLLECTION.findOne(query);
		if (wordInfo == null)
			return 0;
		else
			return (Integer) wordInfo.get("corpusFrequency");
	}

	public int wordFrecuencyInDocument(String word, String docName) {
//		System.out.println("trying to get relevance of " + word + " in " + docName);
		
		BasicDBObject query = new BasicDBObject("word", word);

		DBObject wordInfo = COLLECTION.findOne(query);
		if (wordInfo == null)
			return 0;
		else {
			BasicDBList appearances = (BasicDBList) wordInfo.get("appearances");
			BasicDBObject[] appearancesArray = appearances.toArray(new BasicDBObject[0]);
			for (int i=0; i < appearancesArray.length;i++) {
//				System.out.println("case " + i + " of array: " + appearancesArray[i]);
				if (appearancesArray[i].getString("name").equals(docName)) {
//					System.out.println("found document! " + appearancesArray[i].getInt("tf"));
					return appearancesArray[i].getInt("tf");
				}
			}
//			System.out.println(appearances.toString());
			return 0;
		}
	}

}
