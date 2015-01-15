package request;

import indexer.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.mongodb.DB;

import normalization.WordNormalizer;
import moduleSparql.SparqlModule;
import db.DatabaseManager;

public class Request {

	private DatabaseManager DBManager ;

	private final static int NUMBER_DOCUMENTS = 138;

	public Request() {
		DBManager = new DatabaseManager();
	}
		
//	/**
//	 * Return the list of documents relevant to the specific word
//	 * @param word 
//	 * @return
//	 */
//	private ArrayList<WordRelevance> getRelevantDocs(String word) {
//		ArrayList<WordRelevance> relevantDocs = new ArrayList<WordRelevance>();
//
//		if (DBManager.isInDB(word)) {
//			relevantDocs = DBManager.getWordRelevance(word);
//		}
//		else {
//			System.out.println(word + " not contained in DB");
//			System.out.println("___");
//		}
//
//		return relevantDocs;
//	}
//	
//	/**
//	 * Use WordNormalizer to normalize the words of the request
//	 * @param str
//	 * 		word to normalize
//	 * @return
//	 * 		normalized word
//	 */
//	private String normalizeWord(String str) {
//	    WordNormalizer wordNormalizer = new WordNormalizer();
//	    return wordNormalizer.normalizeWord(str);
//	}
//
//	/**
//	 * Split the request in a String array
//	 * @param request
//	 * 		request to split
//	 * @return
//	 * 		array containing words of the request
//	 */
//	private List<WordRelevance> parseRequest(String request) {
//		Parser parser = new Parser();		
//		
//		List<String> requestList = new ArrayList<String>();
//		String[] requestSplit = request.split("[,]"); //WARNING: if sparql removed, add \\s
//		for (String word : requestSplit) {
//			requestList.add(word);
//		}
//		System.out.println("Request as a list: " + requestList);
//		SparqlModule sparqlModule = new SparqlModule();
//		requestList = sparqlModule.extendRequest(requestList);
//		
//		List<WordRelevance> result = new ArrayList<WordRelevance>();		
//		for (int i = 0; i<requestList.size(); i++) {
//			String word = requestList.get(i);
//			if (!parser.createStopList().contains(word)) {
//				String normalizedWord = normalizeWord(word);				
//				WordRelevance wordRelevance = new WordRelevance(normalizedWord);
//				if (request.contains(word)){
//					wordRelevance.originalWord();
//				}
//				else {
//					wordRelevance.notOriginalWord();
//				}
//				boolean found = false;
//				float foundScore = -1;
//				int index = -1;
//				
//				for(int j = 0 ; j < result.size(); j++) {
//					if(result.get(j).getName().equals(wordRelevance.getName())) {
//						found = true;
//						foundScore = result.get(j).getScore();
//						index = j;
//					}
//				}
//				if (!found) {
//					result.add(wordRelevance);
//				}
//				else {
//					if(foundScore < wordRelevance.getScore()) {
//						result.get(index).setScore(wordRelevance.getScore());
//					}
//				}
//			}
//		}
//		System.out.print("Extended request normalized: [");
//		for(WordRelevance normalizedWord : result) {
//			System.out.print(normalizedWord +", ");
//		}
//		System.out.println("]");
//		return result;
//	}
//	
//	/**
//	 * Retrieve the documents relevant to the request
//	 * @param relevantDocsPerWord
//	 * 		a hashmap which associates to each word of the request the list of relevant documents
//	 * @return
//	 * 		a list of Documents relevant to the request with a score 
//	 */
//	private ArrayList<DocumentRelevance> getRelevantDocsForRequest(
//			HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord) {
//		// list the documents where all the words of the request are present
//		ArrayList<DocumentRelevance> docRelevanceList = new ArrayList<DocumentRelevance>();
//		//list of the documents where only some words of the request are present
//		ArrayList<DocumentRelevance> docRelevanceListAfter = new ArrayList<DocumentRelevance>();
//
//		ArrayList<DocumentRelevance> docRelevanceListZero = new ArrayList<DocumentRelevance>();
//		outerloop:
//		for (int i=1; i<=NUMBER_DOCUMENTS;i++) {
//			String docName = documentNameFormat(i);
//			int weight = 0;
//			int ponderedWeight = 0;
//			
//			boolean someWordIsMissing = false;
////			System.out.println("Document tested: " + docName);
//			for (String word : relevantDocsPerWord.keySet()) {
////				System.out.println("new word tested : " + word );
//				if (getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) == 0) {
////					continue outerloop;
//					someWordIsMissing = true;
//				}
//				weight += getRelevanceForDocument(docName, relevantDocsPerWord.get(word));
////				System.out.println("weighted relevance is : " + getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) * weightedWordFrequencyInCorpus(word));
//				ponderedWeight += getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) * weightedWordFrequencyInCorpus(word);
//			}
////			if (weight > 0) {
//			if (ponderedWeight > 0 && !someWordIsMissing) {
//				docRelevanceList.add(new DocumentRelevance(docName, ponderedWeight/relevantDocsPerWord.keySet().size()));
//			}
//			else if (ponderedWeight > 0) {
//				docRelevanceListAfter.add(new DocumentRelevance(docName, ponderedWeight/relevantDocsPerWord.keySet().size()));
//			}
//			else {
//				docRelevanceListZero.add(new DocumentRelevance(docName, ponderedWeight/relevantDocsPerWord.keySet().size()));
//			}
//		}
//		Collections.sort(docRelevanceList);
//		Collections.sort(docRelevanceListAfter);
//		Collections.sort(docRelevanceListZero);
//		System.out.println("size first list : " + docRelevanceList.size() + " and size second list : " + docRelevanceListAfter.size());
//		docRelevanceList.addAll(docRelevanceList.size(), docRelevanceListAfter);
//		docRelevanceList.addAll(docRelevanceList.size(), docRelevanceListZero);
//		System.out.println("size of list returned is: " + docRelevanceList.size());
//		return docRelevanceList;
//	}
//	
	/**
	 * Give a score to a word based on its frequency in the corpus
	 * @param word
	 * 		word to score
	 * @return
	 * 		a score
	 */
	private float tfidf(String word, String docName) {
		int tf = DBManager.wordFrecuencyInDocument(word, docName);
		int nbDoc = DBManager.wordFrequencyInCorpus(word);
		if (nbDoc == 0) {
			return 0.0f;
		}
		float idf = (float) Math.log10(NUMBER_DOCUMENTS/nbDoc);
		float result = tf*idf;
//		if (nbOccurences >= 100) {
//			result = 1;
//		}
//		else if (nbOccurences >= 80) {
//			result = 2;
//		}
//		else if (nbOccurences >= 50) {
//			result = 3;
//		}
//		else if (nbOccurences >= 20) {
//			result = 5;
//		}
//		else if (nbOccurences >= 10) {
//			result = 6;
//		}
//		else if (nbOccurences >= 5) {
//			result = 7;
//		}
//		else if (nbOccurences == 4) {
//			result = 8;
//		}
//		else if (nbOccurences == 3) {
//			result = 9;
//		}
//		else if (nbOccurences == 2) {
//			result = 10;
//		}
//		else if (nbOccurences == 1) {
//			result = 12;
//		}
//		System.out.println("weight of corpus frequency of " + word + ": " + result + " because : tf: " + tf + " idf: " + idf + " nbDoc " + nbDoc);
		return result;
	}
	
//	/**
//	 * Return the relevance of a word for a specific document
//	 * @param docName
//	 * 		document name
//	 * @param list
//	 * 		list of relevant documents for a word
//	 * @return
//	 * 		relevance
//	 */
//	private int getRelevanceForDocument(String docName, ArrayList<WordRelevance> list) {
//		int occurences = 0;
//		for (WordRelevance relevance : list) {
//			if (docName.equals(relevance.getName())) {
//				occurences = relevance.getOccurrences();
//				break;
//			}
//		}
//		return occurences;
//	}
//	
//	public ArrayList<DocumentRelevance> doRequest(String request) {
//		ArrayList<DocumentRelevance> result = new ArrayList<DocumentRelevance>();
//		
//		HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord = new HashMap<String, ArrayList<WordRelevance>>();
//		List<WordRelevance> wordList = parseRequest(request);
//		
//		for (WordRelevance word : wordList) {
//			relevantDocsPerWord.put(word.getName(), getRelevantDocs(word.getName()));			
//		}
//		
//		result = getRelevantDocsForRequest(relevantDocsPerWord);
//
//		return result;
//	}
	
	private List<String> parseRequest(String request) {
		String[] resultArray = request.split(",");
		List<String> result = new ArrayList<String>();
		for (String word : resultArray) {
			result.add(word);
		}
		return result;
	}
	
	private List<DocumentRelevance> findRelevantDocuments(
			List<WeightedWord> weightedRequest) {
		
		List<DocumentRelevance> result = new ArrayList<DocumentRelevance>();
		
		for (int i = 1; i < NUMBER_DOCUMENTS; i++) {
			System.out.println("Working with document " + i);
			DocumentRelevance docRelevance = new DocumentRelevance(documentNameFormat(i), 0);
			for (WeightedWord ww : weightedRequest) {
				System.out.println("Word " + ww.getNormalizedWord());
				/*
				 * document's relevance to request is based on words relevance to request,
				 *  words relevance in corpus and words relevance to the document
				 */
				float relevanceWordToDocument = ww.getScore() 
						* tfidf(ww.getNormalizedWord(), docRelevance.getName());
				docRelevance.addScore(relevanceWordToDocument);
				System.out.println("Added " + relevanceWordToDocument + " to document score");
			}
			System.out.println("Document " + documentNameFormat(i) + " relevance is : " + docRelevance.getNormalizedScore());
			result.add(docRelevance);
		}
		Collections.sort(result);
		return result;
	}
	
	public List<DocumentRelevance> performRequest(String request) {
		System.out.println("Request: " + request);
		List<String> parsedRequest = parseRequest(request);
		System.out.println("Parsed request: " + parsedRequest);
		
		SparqlModule sparqlModule = new SparqlModule();
		List<String> extendedRequest = sparqlModule.extendRequest(parsedRequest);
		
		List<WeightedWord> weightedRequest = new ArrayList<WeightedWord>();
		for (String word : extendedRequest) {
			WeightedWord ww = new WeightedWord(word, request.contains(word)); // its score is based on if it was present or not in the first request
			weightedRequest.add(ww);
		}
		System.out.println("Constructed request: " + weightedRequest);
		List<DocumentRelevance> result = findRelevantDocuments(weightedRequest);
		
		return result;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		Request request = new Request();
		Scanner sc = new Scanner(System.in);
		String words;

		// User request
		System.out.println("Enter your keywords");
		words = sc.nextLine();
		
		for (DocumentRelevance docRelevance : request.performRequest(words)) {
			System.out.println(docRelevance.getName() + " " + docRelevance.getNormalizedScore());
		}
		
		sc.close();
	}	
	
	

//	public static void main(String[] args) {
//		Request request = new Request();
//
//		HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord = new HashMap<String, ArrayList<WordRelevance>>();
//
//		Scanner sc = new Scanner(System.in);
//		String words;
//
//		// User request
//		System.out.println("Enter your keywords");
//		words = sc.nextLine();
//		List<WordRelevance> wordList = request.parseRequest(words);
//
//		for (String word : wordList) {
//			relevantDocsPerWord.put(word, request.getRelevantDocs(word)) ;
//			System.out.println("word : " + word);
//			System.out.println("relevant docs : " );
//			for (WordRelevance relevance : relevantDocsPerWord.get(word)) {
//				System.out.println(relevance.getName());
//			}
//		}
//
//		ArrayList<DocumentRelevance> docRelevanceList = request.getRelevantDocsForRequest(relevantDocsPerWord);
//		System.out.println("result before sorting_____________");
//		for (DocumentRelevance docRelevance : docRelevanceList) {
//			System.out.println(docRelevance.getName() + " : " + docRelevance.getScore());
//		}
//		
//		Collections.sort(docRelevanceList);
//		System.out.println("result_____________________________");
//		for (DocumentRelevance docRelevance : docRelevanceList) {
//			System.out.println(docRelevance.getName() + " : " + docRelevance.getScore());
//		}
//
//		sc.close();
//	}	
	
	

	private static String documentNameFormat(int docId) {
		return "D"+docId+".html";
	}
}
