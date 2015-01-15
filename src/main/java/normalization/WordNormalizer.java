package normalization;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class WordNormalizer {

	public static String normalizeWord(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    String result = pattern.matcher(nfdNormalizedString).replaceAll("");
	    result = result.toLowerCase();
	    if (result.endsWith("s")) {
	    	result = result.substring(0, result.length() - 1);
	    }
	    if (result.length() >= 7) {
	    	result = result.substring(0, 7);
	    }
	    return result;
//		return str;
	}
}
