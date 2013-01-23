package at.ac.tuwien.inso.esse.advancedsecsys.client.mitm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NumbersToReplace {
	private static final String SEPARATOR = ";";

	private Map<String, String> replacementMap = new HashMap<String, String>();

	public NumbersToReplace(File file) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		try {
			for (String line; (line = fileReader.readLine()) != null;) {
				String[] tokens = line.split(SEPARATOR);
				if (replacementMap.put(tokens[0], tokens[1]) != null) {
					System.err.println("Double item: " + tokens[0]);
				}
			}
		} finally {
			fileReader.close();
		}
	}

	public String getTelNr(String name) {
		return replacementMap.get(name);
	}
}
