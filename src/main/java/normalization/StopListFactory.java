package normalization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import configuration.Configuration;

public class StopListFactory {
	
	private HashSet<String> stopList;
	
	public StopListFactory() {
		BufferedReader br = null;
		stopList = new HashSet<String>();

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(Configuration.PATH_TO_STOPLIST));

			while ((sCurrentLine = br.readLine()) != null) {
				stopList.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean isInStopList(String word) {
		return stopList.contains(word);
	}

}
