package moduleSparql;

import java.util.Map;

public class SparqlModule {
	
	private SparqlClient sparqlClient;
	
	public SparqlModule() {
		sparqlClient = new SparqlClient("localhost:3030/space");
	}
	
	public String[] extendRequest(String[] request) {
		String[] extendedRequest = null;
		SparqlClient sparqlClient = new SparqlClient("localhost:3030/space");

        String query = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        if (serverIsUp) {
            System.out.println("server is UP");
        }
        else {
        	System.out.println("Server is down");
        }
		return extendedRequest;
	}
	
	private String[] returnLabels(String[] request) {
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT ?labels WHERE\n"
                + "{\n"
                + "    ?subject rdfs:label ?object.\n"
                + "}\n"
                + "GROUP BY ?piece\n";
        Iterable<Map<String, String>> results = sparqlClient.select(query);
        System.out.println("nombre de personnes par pièce:");
        for (Map<String, String> result : results) {
            System.out.println(result.get("subject") + " : " + result.get("label"));
            
        }
	}

}
