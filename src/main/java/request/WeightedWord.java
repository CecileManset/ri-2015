package request;

import normalization.WordNormalizer;

public class WeightedWord implements Comparable<WeightedWord> {
	
	private String word;
	private String normalizedWord;
	private float score;
	
	public WeightedWord(String word, boolean inRequest) {
		this.word = word;
		this.normalizedWord = WordNormalizer.normalizeWord(word);
		if (inRequest) {
			this.score = 1.0f;
		}
		else {
			this.score = 0.3f;
		}
	}

	public int compareTo(WeightedWord weightedWord) {
		return (int) (this.score - weightedWord.score);
	}

	public String getWord() {
		return word;
	}

	public String getNormalizedWord() {
		return normalizedWord;
	}

	public float getScore() {
		return score;
	}
	
	public String toString() {
		return "[" + this.normalizedWord + ";" + this.score +"]";
	}

}
