package request;

public class WordRelevance {

	private String name ;
	private int tf ;
	
	public WordRelevance() {
		name = new String();
		tf = 1;
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
}
