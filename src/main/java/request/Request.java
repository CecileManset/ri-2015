package request;

import indexer.Parser;
import indexer.WordNormalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import moduleSparql.SparqlModule;
import db.DatabaseManager;

public class Request {

	private DatabaseManager DBManager ;

	private final static int NUMBER_DOCUMENTS = 138;

	public Request() {
		DBManager = new DatabaseManager();
	}
		
	/**
	 * Return the list of documents relevant to the specific word
	 * @param word 
	 * @return
	 */
	private ArrayList<WordRelevance> getRelevantDocs(String word) {
		ArrayList<WordRelevance> relevantDocs = new ArrayList<WordRelevance>();

		if (DBManager.isInDB(word)) {
			relevantDocs = DBManager.getWordRelevance(word);
		}
		else {
			System.out.println(word + " not contained in DB");
			System.out.println("___");
		}

		return relevantDocs;
	}
	
	/**
	 * Use WordNormalizer to normalize the words of the request
	 * @param str
	 * 		word to normalize
	 * @return
	 * 		normalized word
	 */
	private String normalizeWord(String str) {
	    WordNormalizer wordNormalizer = new WordNormalizer();
	    return wordNormalizer.normalizeWord(str);
	}

	/**
	 * Split the request in a String array
	 * @param request
	 * 		request to split
	 * @return
	 * 		array containing words of the request
	 */
	private String[] parseRequest(String request) {
		Parser parser = new Parser();
		
		
		List<String> requestList = new ArrayList<String>();
		String[] requestSplit = request.split("[,]"); //WARNING: if sparql removed, add \\s
		for (String word : requestSplit) {
			requestList.add(word);
		}
		System.out.println("Request as a list: " + requestList);
		SparqlModule sparqlModule = new SparqlModule();
		requestList = sparqlModule.extendRequest(requestList);
		
		List<String> result = new ArrayList<String>();		
		for (int i = 0; i<requestList.size(); i++) {
			String word = requestList.get(i);
			if (!parser.createStopList().contains(word)) {
				String normalizedWord = normalizeWord(word);
				if (!result.contains(normalizedWord)) {
					result.add(normalizedWord);
				}
			}
		}
		System.out.print("Extended request normalized: [");
		for(String normalizedWord : result) {
			System.out.print(normalizedWord +", ");
		}
		System.out.println("]");
		return result.toArray(new String[result.size()]);
	}

//	private ArrayList<WordRelevance> intersection(ArrayList<WordRelevance> list1, ArrayList<WordRelevance> list2) {
//		ArrayList<WordRelevance> list = new ArrayList<WordRelevance>();
//
//		for (WordRelevance relevance1 : list1) {
//			for (WordRelevance relevance2 : list2) {
//				if (relevance1.getName().equals(relevance2.getName())) {
//					list.add(relevance1);
//				}
//			}
//		}
//
//		return list;
//	}
	
	/**
	 * Retrieve the documents relevant to the request
	 * @param relevantDocsPerWord
	 * 		a hashmap which associates to each word of the request the list of relevant documents
	 * @return
	 * 		a list of Documents relevant to the request with a score 
	 */
	private ArrayList<DocumentRelevance> getRelevantDocsForRequest(
			HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord) {
		// list the documents where all the words of the request are present
		ArrayList<DocumentRelevance> docRelevanceList = new ArrayList<DocumentRelevance>();
		//list of the documents where only some words of the request are present
		ArrayList<DocumentRelevance> docRelevanceListAfter = new ArrayList<DocumentRelevance>();

		outerloop:
		for (int i=1; i<=NUMBER_DOCUMENTS;i++) {
			String docName = documentNameFormat(i);
			int weight = 0;
			int ponderedWeight = 0;
			
			boolean someWordIsMissing = false;
//			System.out.println("Document tested: " + docName);
			for (String word : relevantDocsPerWord.keySet()) {
//				System.out.println("new word tested : " + word );
				if (getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) == 0) {
//					continue outerloop;
					someWordIsMissing = true;
				}
				weight += getRelevanceForDocument(docName, relevantDocsPerWord.get(word));
//				System.out.println("weighted relevance is : " + getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) * weightedWordFrequencyInCorpus(word));
				ponderedWeight += getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) * weightedWordFrequencyInCorpus(word);
			}
//			if (weight > 0) {
			if (ponderedWeight > 0 && !someWordIsMissing) {
				docRelevanceList.add(new DocumentRelevance(docName, ponderedWeight/relevantDocsPerWord.keySet().size()));
			}
			else if (ponderedWeight > 0) {
				docRelevanceListAfter.add(new DocumentRelevance(docName, ponderedWeight/relevantDocsPerWord.keySet().size()));
			}
		}
		Collections.sort(docRelevanceList);
		Collections.sort(docRelevanceListAfter);
		System.out.println("size first list : " + docRelevanceList.size() + " and size second list : " + docRelevanceListAfter.size());
		docRelevanceList.addAll(docRelevanceList.size(), docRelevanceListAfter);
		return docRelevanceList;
	}
	
	/**
	 * Give a score to a word based on this frequency in the corpus
	 * @param word
	 * 		word to score
	 * @return
	 * 		a score
	 */
	private int weightedWordFrequencyInCorpus(String word) {
		int nbOccurences = DBManager.wordFrequencyInCorpus(word);
		if (nbOccurences >= 100) {
			return 1;
		}
		else if (nbOccurences >= 80) {
			return 2;
		}
		else if (nbOccurences >= 50) {
			return 3;
		}
		else if (nbOccurences >= 20) {
			return 5;
		}
		else if (nbOccurences >= 10) {
			return 8;
		}
		else if (nbOccurences >= 5) {
			return 13;
		}
		else if (nbOccurences == 4) {
			return 21;
		}
		else if (nbOccurences == 3) {
			return 34;
		}
		else if (nbOccurences == 2) {
			return 55;
		}
		else if (nbOccurences == 1) {
			return 89;
		}
		return 0;
	}
	
	/**
	 * Return the relevance of a word for a specific document
	 * @param docName
	 * 		document name
	 * @param list
	 * 		list of relevant documents for a word
	 * @return
	 * 		relevance
	 */
	private int getRelevanceForDocument(String docName, ArrayList<WordRelevance> list) {
		int occurences = 0;
		for (WordRelevance relevance : list) {
			if (docName.equals(relevance.getName())) {
				occurences = relevance.getOccurrences();
				break;
			}
		}
		return occurences;
	}
	
	public ArrayList<DocumentRelevance> doRequest(String request) {
		ArrayList<DocumentRelevance> result = new ArrayList<DocumentRelevance>();
		
		HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord = new HashMap<String, ArrayList<WordRelevance>>();
		String[] wordList = parseRequest(request);
		
		for (String word : wordList) {
			relevantDocsPerWord.put(word, getRelevantDocs(word));			
		}
		
		result = getRelevantDocsForRequest(relevantDocsPerWord);
		

		return result;
	}

	public static void main(String[] args) {
		Request request = new Request();

		HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord = new HashMap<String, ArrayList<WordRelevance>>();

		Scanner sc = new Scanner(System.in);
		String words;

		// User request
		System.out.println("Enter your keywords");
		words = sc.nextLine();
		String[] wordList = request.parseRequest(words);

		for (String word : wordList) {
			relevantDocsPerWord.put(word, request.getRelevantDocs(word)) ;
			System.out.println("word : " + word);
			System.out.println("relevant docs : " );
			for (WordRelevance relevance : relevantDocsPerWord.get(word)) {
				System.out.println(relevance.getName());
			}
		}

		ArrayList<DocumentRelevance> docRelevanceList = request.getRelevantDocsForRequest(relevantDocsPerWord);
		System.out.println("result before sorting_____________");
		for (DocumentRelevance docRelevance : docRelevanceList) {
			System.out.println(docRelevance.getName() + " : " + docRelevance.getScore());
		}
		
		Collections.sort(docRelevanceList);
		System.out.println("result_____________________________");
		for (DocumentRelevance docRelevance : docRelevanceList) {
			System.out.println(docRelevance.getName() + " : " + docRelevance.getScore());
		}

		sc.close();
	}	
	
	private static String documentNameFormat(int docId) {
		return "D"+docId+".html";
	}
}
