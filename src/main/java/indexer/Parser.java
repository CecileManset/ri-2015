package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import configuration.Configuration;
import request.WordRelevance;
import db.DatabaseManager;


public class Parser {

	//private static Logger LOGGER = Logger.getLogger(Parser.class);
	private static String DELIMITERS = "[.,;?!-: ]+";
	
	private final static int NUMBER_DOCUMENTS = 138;
		
	public Parser() {
	}

	private String[] extractTextFromHtmlDoc(int id) {
		File input = new File(Configuration.PATH_TO_CORPUS + id + ".html");
		String text = null;
		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			text = doc.text().toLowerCase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text.split(DELIMITERS);
	}

	private HashSet<String> createStopList() {
		BufferedReader br = null;
		HashSet<String> stopList = new HashSet<String>();

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(Configuration.PATH_TO_STOPLIST));

			while ((sCurrentLine = br.readLine()) != null) {
				stopList.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return stopList;
	}
	
	public String normalizeWord(String word) {
		WordNormalizer wordNormalizer = new WordNormalizer();
		return wordNormalizer.normalizeWord(word);
	}

	public static void main(String[] args) {
		Parser parser = new Parser();

		db.DatabaseManager dbManager = new DatabaseManager();
		HashSet<String> stopList = parser.createStopList();

		// to a word, we associate a list of documents where it is present with some information (=> wordRelevance object)
		HashMap<String, ArrayList<WordRelevance>> wordsMap = new HashMap<String, ArrayList<WordRelevance>>();
		
		// to a word, we associate the number of times it is present in the corpus
		HashMap<String, Integer> wordsIdf = new HashMap<String, Integer>();
		
		// for each document in the corpus
		for (int docId = 1; docId <= NUMBER_DOCUMENTS; docId++) {
			// for each word of a given document
			for (String wordHtml : parser.extractTextFromHtmlDoc(docId)) {
				// if this word is not in the stoplist
				if (!stopList.contains(wordHtml)) {
					// if this word is in the wordsMap
					wordHtml = parser.normalizeWord(wordHtml);
					if (wordsMap.containsKey(wordHtml)) {						
						// if this word was already in the document we increment the occurrence (in wordAlreadyInDocument)
						// otherwise 
						if (!wordAlreadyInDocument(wordsMap, wordHtml, docId)) {
							// we add a new occurrence 
							wordsMap.get(wordHtml).add(new WordRelevance(documentNameFormat(docId)));
							// this word appears in this document, so we increment the number of documents it appears in
							wordsIdf.put(wordHtml, wordsIdf.get(wordHtml) + 1);
						}
						
						
					}
					// if this word was not in a document
					else {
						ArrayList<WordRelevance> listWordRelevance = new ArrayList<WordRelevance>();
						listWordRelevance.add(new WordRelevance(documentNameFormat(docId)));
						wordsMap.put(wordHtml, listWordRelevance);
						wordsIdf.put(wordHtml, 1);
					}
				}
			}
		}
		
		for (String word : wordsMap.keySet()) {
			System.out.println("word : " + word + " appears in : ");
			JSONObject wordJSON = new JSONObject();
			wordJSON.put("word", word);
			wordJSON.put("corpusFrequency", wordsIdf.get(word));
			JSONArray documentsArray = new JSONArray();
			for (WordRelevance relevance : wordsMap.get(word)) {
				JSONObject relevanceJSON = new JSONObject();
				relevanceJSON.put("name", relevance.getName());
				relevanceJSON.put("tf", relevance.getOccurrences());
				documentsArray.put(relevanceJSON);
				System.out.println("document : " + relevance.getName() + " tf : " + relevance.getOccurrences());
			}
			wordJSON.put("appearances", documentsArray);
			System.out.println(wordJSON.toString());
			dbManager.insertDBEntry(wordJSON);
		}
	}

	private static boolean wordAlreadyInDocument(
			HashMap<String, ArrayList<WordRelevance>> wordsMap, String wordHtml, int docId) {
		for (WordRelevance wordRelevance : wordsMap.get(wordHtml)) {
			if (wordRelevance.getName().equals(documentNameFormat(docId))) {
				wordRelevance.addOccurence();
				return true;
			}
		}
		return false;
	}

	private static String documentNameFormat(int docId) {
		return "D"+docId+".html";
	}

}
