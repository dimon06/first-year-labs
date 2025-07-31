import java.io.*;
import java.util.*;

public class WordStatWordsShingles {

    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];

        Map<String, Integer> wordCount = new TreeMap<>(Comparator.reverseOrder());

        try {
        	MyScanner scanner = new MyScanner(inputFile);
            scanner.setCheckerWord(sym -> Character.isLetter(sym) || sym == '\'' 
            || Character.getType(sym) == Character.DASH_PUNCTUATION);
        	try {
        		while (!scanner.isEnd()) {
		        	if (scanner.hasNextWord()) {
						String word = (scanner.nextWord()).toLowerCase();
			        	if (word.length() < 3) {
			        		update(wordCount, word);
			        	} else {
			        		for (int i = 0; i <= word.length()-3; i++) {
			        			update(wordCount, word.substring(i, i+3));
			        		}
			        	}
		        	}
		        }
        	} finally {
        		scanner.close();
        	}
        }  catch (FileNotFoundException e) {
        	System.err.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
        	System.err.println("Error when reading file: " + e.getMessage());
        }

        try {
        	Writer out = new OutputStreamWriter(
        			new FileOutputStream(outputFile),
        			"utf8"
        		);
        	try {
        		for (String str : wordCount.keySet()) {
                	out.write(str + " " + wordCount.get(str) + "\n");
            	}
        	} finally {
        		out.close();
        	}
    	} catch (FileNotFoundException e) {
        	System.err.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
        	System.err.println("Error when writing file: " + e.getMessage());
        }
    }
    public static void update(Map<String, Integer> wordCount, String word) {
    	var cnt = wordCount.get(word);
    	if (cnt != null) {
    		wordCount.put(word, cnt + 1);
    	} else {
    		wordCount.put(word, 1);
		}
    }
}
