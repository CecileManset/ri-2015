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
				+ "	   FILTER (lang(?label) = \"fr\")"
				+ "}\n";
//		System.out.println(query);
		//		Iterable<Map<String, String>> results = sparqlClient.select(query);
		List<String> results = sparqlClient.selectList(query);
		System.out.println("labels du mot [" + word + "] : " + results.toString());
		return results;

	}

}
