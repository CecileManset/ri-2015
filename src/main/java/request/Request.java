package request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import db.DatabaseManager;
import evaluator.Evaluation;

public class Request {

	private DatabaseManager DBManager ;

	private final static int NUMBER_DOCUMENTS = 138;

	public Request() {
		DBManager = new DatabaseManager();
	}
	// Retrieve relevant documents associated to word from DB
	// Return list of relevant documents
	private ArrayList<WordRelevance> getRelevantDocs(String word) {//, int nbRelevantDocs) {
		ArrayList<WordRelevance> relevantDocs = new ArrayList<WordRelevance>();

		if (DBManager.isInDB(word)) {
			ArrayList<WordRelevance> infoList = DBManager.getWordRelevance(word);

			for (WordRelevance docInfo : infoList) {
				relevantDocs.add(docInfo);
			}
		}
		else {
			System.out.println(word + " not contained in DB");
			System.out.println("___");
		}

		return relevantDocs;
	}

	private String[] parseRequest(String request) {
		return request.split(" ");
	}

	private ArrayList<WordRelevance> intersection(ArrayList<WordRelevance> list1, ArrayList<WordRelevance> list2) {
		ArrayList<WordRelevance> list = new ArrayList<WordRelevance>();

		for (WordRelevance relevance1 : list1) {
			for (WordRelevance relevance2 : list2) {
				if (relevance1.getName().equals(relevance2.getName())) {
					list.add(relevance1);
				}
			}
		}

		return list;
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
		
//		Evaluation evalution = new Evaluation();
//		evalution.evaluate(docRelevanceList, 1);
		

		//		ArrayList<WordRelevance> listDocuments = request.computeIntersectionDocuments(null, wordList);
		//		if (listDocuments.isEmpty()) {
		//			
		//		}
		//		else {
		////			sortList(listDocuments);
		//			request.printList(listDocuments);
		//		}

		sc.close();
	}

	private ArrayList<DocumentRelevance> getRelevantDocsForRequest(
			HashMap<String, ArrayList<WordRelevance>> relevantDocsPerWord) {
		ArrayList<DocumentRelevance> docRelevanceList = new ArrayList<DocumentRelevance>();

		for (int i=1; i<=NUMBER_DOCUMENTS;i++) {
			String docName = documentNameFormat(i);
			int weight = 0;
			System.out.println("number of words : " + relevantDocsPerWord.keySet().size());
			for (String word : relevantDocsPerWord.keySet()) {
				System.out.println("new word tested " + i );
//				if (getRelevanceForDocument(docName, relevantDocsPerWord.get(word)) == 0) {
//					System.out.println("toooooootooooooooo");
//					break;
//				}
				System.out.println("couuuuuuuuuuuuuuuuuucouuuuuuuuuuuuuuuuuu" + getRelevanceForDocument(docName, relevantDocsPerWord.get(word)));
				weight += getRelevanceForDocument(docName, relevantDocsPerWord.get(word));
			}
			if (weight > 0) {
				docRelevanceList.add(new DocumentRelevance(docName, weight/relevantDocsPerWord.keySet().size()));
			}
		}
		return docRelevanceList;
	}
	
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

	private void printList(ArrayList<WordRelevance> listDocuments) {
		for (WordRelevance relevance : listDocuments) {
			System.out.println(relevance.getName());
		}
	}

//	//TODO: a recommencer, c'est n'importe quoi !
//	private ArrayList<WordRelevance> computeIntersectionDocuments(ArrayList<WordRelevance> list, String[] wordList) {
//		Request request = new Request();
//		if (list == null) {
//			list = request.getRelevantDocs(wordList[0]);
//			ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(wordList));
//			list1.remove(0);
//			request.computeIntersectionDocuments(list, list1.toArray(wordList));
//			return list;
//		}
//		if (wordList.length == 0) {
//			return list;
//		}
//		else if (wordList.length == 1) {
//			return request.getRelevantDocs(wordList[0]);
//		}
//		else {
//			System.out.println("compute : " + list.size() + " " + wordList.length);
//
//			list = request.intersection(list, request.getRelevantDocs(wordList[0]));
//			ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(wordList));
//			list1.remove(0);
//			request.computeIntersectionDocuments(list, list1.toArray(wordList));
//			return list;
//		}
//	}
	
	private static String documentNameFormat(int docId) {
		return "D"+docId+".html";
	}
}

// TODO: compare several result list from requests with several key words to return the list of documents
// present in all the lists
// TODO: retrieve most relevant docs


