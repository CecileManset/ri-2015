package moduleSparql;

import java.util.ArrayList;
import java.util.List;

public class SparqlModule {

	private SparqlClient sparqlClient;

	public SparqlModule() {
		sparqlClient = new SparqlClient("localhost:3030/space");
	}

	public List<String> extendRequest(List<String> request) {
		List<String> extendedRequest = new ArrayList<String>();
		extendedRequest.addAll(request);
		List<String> result = new ArrayList<String>();
		SparqlClient sparqlClient = new SparqlClient("localhost:3030/space");

		String query = "ASK WHERE { ?s ?p ?o }";
		boolean serverIsUp = sparqlClient.ask(query);
		if (serverIsUp) {
			for (String word : request) {
				extendedRequest.addAll(getLabelsAndSubClasses(word));
			}
			extendedRequest.addAll(getRequestResults(request));




			for (String word : extendedRequest) {
				String[] wordList = word.split(" ");
				for(int i = 0 ; i < wordList.length ; i++) {
					result.add(wordList[i]);
				}
			}
		}
		else {
			System.out.println("Server is down");
		}
		System.out.println("Extended request: " + result.toString());
		return result;
	}

	private List<String> getLabelsAndSubClasses(String word) {
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "SELECT DISTINCT ?label WHERE\n"
				+ "{\n"
				+ "    ?class rdfs:label \"" + word + "\"@fr.\n"
				+ "    ?subClass rdfs:subClassOf ?class .\n"
				+ "    ?subClass rdfs:label ?label. \n"
				+ "	   FILTER (lang(?label) = \"fr\" || lang(?label) = \"\")"
				+ "}\n";
//		System.out.println(query);
		//		Iterable<Map<String, String>> results = sparqlClient.select(query);
		List<String> results = sparqlClient.selectList(query);
		System.out.println("labels du mot [" + word + "] : " + results.toString());
		return results;

	}
	
	/**
	 * 	Creates query with proper subject and property parameters (for the general request of "getRequestResult")
	 */
	private String doQuery(String subject, String property) {
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "SELECT DISTINCT ?labels WHERE\n"
				+ "{\n"
				+ "    ?subject rdfs:label ?labelSubject.\n"
				+ "    FILTER(contains(?labelSubject, \"" + subject + "\")).\n"
				+ "    ?property rdfs:label ?labelProperty.\n"
				+ "    FILTER(contains(?labelProperty, \"" + property + "\")).\n"
				+ "    ?subject ?property ?results .\n"
				+ "    ?results rdfs:label ?labels. \n"
				+ "	   FILTER (lang(?labels) = \"fr\" || lang(?labels) = \"\")"
				+ "}\n";
		
		return query;
	}
	
	private List<String> getListIntersection(List<String> list1, List<String> list2) {
		List<String> result = new ArrayList<String>();
		List<String> longestList = list1;
		List<String> shortestList = list2;
		
		if (list1.size() != 0 && list2.size() != 0) {
			if (list1.size() < list2.size()) {
				longestList = list2;
				shortestList = list1;
			}
			
			for (String word : longestList) {
				if (shortestList.contains(word)) {
					result.add(word);
				}
			}
		}
		else {
			result.addAll(list1);
			result.addAll(list2);
		}
		
		return result;
	}
	
	/**
	 * 2 different strategies of placing the words in the SPARQL request depending on the request length
	   for a short request (less than 4 words), each word is subject and the next one is property for all words in request
	   for a long request (more or equal 4 words), the first word is subject and the second one is property, then the third one is property
	   we then check the results and keep the results in both requests
	 */
	private List<String> getRequestResults(List<String> request) {
		String subject = new String();
		String property = new String();
		List<String> results = new ArrayList<String>();
		
		if (request.size() < 4) {
			for (int i = 0 ; i < request.size()-1 ; i++) {
				property = request.get(i);
				subject = request.get(i+1);
				
				results.addAll(sparqlClient.selectList(doQuery(subject, property)));
			}
		}
		else {
			property = request.get(0);
			subject = request.get(1);
			
			results = sparqlClient.selectList(doQuery(subject, property));
			
			property = request.get(0);
			subject = request.get(2);
			
			results = getListIntersection(results, sparqlClient.selectList(doQuery(subject, property)));
		}

		System.out.println("résultats de la requête [" + request.toString() + "] : " + results.toString());
		return results;
	}
}
