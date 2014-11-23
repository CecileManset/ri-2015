package evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import configuration.Configuration;
import request.DocumentRelevance;
import request.Request;

public class Evaluation {

	private static int DOC_NB = 138;
	private static int REQ_NB = 9;
	
	private String[] requestTab = new String[REQ_NB];

	public Evaluation() {
		File input = new File(Configuration.PATH_TO_REQUESTS);
		Scanner sc = null;
		int ind = 0;
		
		try {
			sc = new Scanner(input);
			while (sc.hasNextLine()) {
				requestTab[ind] = sc.nextLine();
				ind++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
	
	// Renvoie un tableau qui fait correspondre l'indice de pertinence établi par un humain à chaque document du corpus, 
	// pour une requête donnée. Attention, le tableau commence en 0 et les docs en 1 !!
	private float[] readQrel(int qrelId) {
		float[] docRelevanceTab = new float[DOC_NB];
		int ind = 0;
		String relevance;
		
		File input = new File(Configuration.PATH_TO_QREL + "qrelQ" + qrelId + ".txt");
		Scanner sc = null;
		
		try {
			sc = new Scanner(input);
			while (sc.hasNext()) {
				sc.next(); // Nom du document
				relevance = sc.next();
				if (relevance.compareTo("0,5") == 0)
					relevance = "0.5";
				docRelevanceTab[ind] = Float.parseFloat(relevance);
				ind++;
			}
			if (ind != DOC_NB) {
				System.out.println("WARNING : an error might have occurred. The Qrel file was weirdly parsed.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return docRelevanceTab;
	}
	
	private float computePerformance(ArrayList<DocumentRelevance> docRelevanceList, float[] docRelevanceTab, float nbRelevantDocs) {
		float performance = 0;
		String docName;
		int docNb;
		
		float factor = 1/nbRelevantDocs;
		
		for (int i = 0 ; i < nbRelevantDocs ; i++) {
			docName = docRelevanceList.get(i).getName();
			docNb = Integer.parseInt(docName.substring(1).split("\\.")[0]); // Testé 
			
			performance += docRelevanceTab[docNb-1] * factor;
		}
		
		return performance;
	}

	// run all the requests and check against Qrel file to establish accuracy
	public void evaluate(ArrayList<DocumentRelevance> docRelevanceList, int qrelId) {
		DecimalFormat df = new DecimalFormat("#.##");
		float[] docRelevanceTab = readQrel(qrelId);

		docRelevanceTab = readQrel(qrelId);
		System.out.println("Request " + qrelId + ": \""+ requestTab[qrelId] + " \"");
		System.out.println("Précision à 5 : " + df.format(computePerformance(docRelevanceList, docRelevanceTab, 5)));
		System.out.println("Précision à 10 : " + df.format(computePerformance(docRelevanceList, docRelevanceTab, 10)));
		System.out.println("Précision à 25 : " + df.format(computePerformance(docRelevanceList, docRelevanceTab, 25)));
		System.out.println("_________________________");
	}
	
	public static void main(String[] args) {
		Evaluation eval = new Evaluation();
		Request req = new Request();
		
		for (int i = 1 ; i <= REQ_NB ; i++) {
			ArrayList<DocumentRelevance> docRelevanceList = req.doRequest(eval.requestTab[i-1]);
			eval.evaluate(docRelevanceList, i);
		}
	}
}
