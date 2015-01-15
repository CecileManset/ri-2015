package request;
/**
 * Reference of a word for a document... or a request
 * @author Nicolas
 *
 */
public class WordRelevance {

	private String name ;
	private int tf ;
	private float score;
	
	public void setScore(float score) {
		this.score = score;
	}

	public WordRelevance() {
		name = new String();
		tf = 1;
		score = 0.0f;
	}
	
	public WordRelevance(String name) {
		this.name = name;
		this.tf = 1;
	}
	
	public String getName() {
		return name;
	}

	public int getOccurrences() {
		return tf;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOccurrences(int occurrences) {
		this.tf = occurrences;
	}
	
	public void addOccurence() {
		this.tf++;
	}
	
	public String toString() {
		return "name : " + name + ", times : " + tf;
	}
	
	public float getScore() {
		return this.score;
	}
	
	public void originalWord() {
		this.score = 1.0f;
	}
	
	public void notOriginalWord() {
		this.score = 0.5f;
	}
}
