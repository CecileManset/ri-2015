package request;

public class DocumentRelevance implements Comparable<DocumentRelevance> {
	
	private String name ;
	private int score;
	private float normalizedScore;

	public DocumentRelevance(String name, int score) {
		super();
		this.name = name;
		this.score = score;
	}
	
	public DocumentRelevance(String name, float normalizedScore) {
		super();
		this.name = name;
		this.normalizedScore = normalizedScore;
	}
	
	public float getNormalizedScore() {
		return normalizedScore;
	}

	public void setNormalizedScore(float normalizedScore) {
		this.normalizedScore = normalizedScore;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public int compareTo(DocumentRelevance docRelevance) {
		return docRelevance.getScore() - score;
	}

}
