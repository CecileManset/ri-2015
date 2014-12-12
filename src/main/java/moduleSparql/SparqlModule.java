package moduleSparql;

public class SparqlModule {
	
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

}
