package evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import request.DocumentRelevance;

public class Evaluation {

	private static String PATH_TO_QREL = "C:\\Users\\Nicolas\\Downloads\\qrels\\";

//	private ArrayList<DocumentRelevance> qrels;

	public Evaluation() {
//		qrels = new ArrayList<DocumentRelevance>();
//		for (int qrelId = 1; qrelId <= 9; qrelId++) {
//			File input = new File(PATH_TO_QREL + "qrelQ" + qrelId + ".txt");
//			Scanner sc = null;
//			try {
//				sc = new Scanner(input);
//				while (sc.hasNextLine()) {
//					String[] qrel = sc.next().split(" ");
//					qrels.add(new DocumentRelevance(qrel[0], Float.parseFloat(qrel[1])));
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} finally {
//				sc.close();
//			}
//		}
	}

	private ArrayList<DocumentRelevance> createQrel(int qrelId) {
		ArrayList<DocumentRelevance> qrels = new ArrayList<DocumentRelevance>();
		File input = new File(PATH_TO_QREL + "qrelQ" + qrelId + ".txt");
		Scanner sc = null;
		try {
			sc = new Scanner(input);
			while (sc.hasNextLine()) {
//				String[] qrel = sc.next().split("[ ]+|\t");
//				System.out.println("toto " + qrel.length);
				qrels.add(new DocumentRelevance(sc.next(), Float.parseFloat(sc.next())));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return qrels;
	}

	public void evaluate(ArrayList<DocumentRelevance> docRelevanceList, int qrelId) {
		System.out.println("Qrels list: ");
		for (DocumentRelevance docRelevance : createQrel(qrelId)) {
			System.out.println(docRelevance.getName() + " " + docRelevance.getNormalizedScore());
		}
		System.out.println("Our list: ");
		for (DocumentRelevance docRelevance : docRelevanceList) {
			System.out.println(docRelevance.getName() + " " + docRelevance.getScore());
		}
	}

}
