import java.io.*;


public class MyScanner {

    private final int sizeBuffer = 1024;
    private final String separator = System.lineSeparator();
    private Reader reader;
    private char[] buffer = new char[sizeBuffer];
    private int beginInput;
    private int endInput;
    private int lineNumber;
    private CharChecker CheckerNumber, CheckerWord;

    public MyScanner(InputStream input) throws IOException {
        reader = new InputStreamReader(input, "utf8");
        lineNumber = 0;
        readBuffer();
    }

    public MyScanner(String fileName) throws FileNotFoundException, IOException {
        reader = new InputStreamReader(new FileInputStream(fileName), "utf8");
        lineNumber = 0;
        readBuffer();
    }

    public void setCheckerNumber(CharChecker checker) {
        this.CheckerNumber = checker;
    }

    public void setCheckerWord(CharChecker checker) {
        this.CheckerWord = checker;
    }
    
    public boolean hasNextInt() throws IOException {
        return hasNext("number");
    }

    public boolean hasNextWord() throws IOException {
        return hasNext("word");
    }

    private boolean hasNext(String type) throws IOException {
        while (!isEnd() && !isSymbol(type, buffer[beginInput])) {
            if (hasSeparator()) {
                return false;
            }
            updateBuffer();
        }
        return (!isEnd());
    }

    public int nextInt() throws IOException {
        return pars(next("number"));
    }

    private int pars(String number) {
        int begin = 0, end = number.length();
        int zn = 1;
        if (number.charAt(0) == '-') {
            zn = -1;
            begin++;
        }
        int mn = 10;
        if (Character.toLowerCase(number.charAt(number.length() - 1)) == 'o') {
            mn = 8;
            end--;
        }
        int res = 0;
        while (begin < end) {
            res = res*mn+(number.charAt(begin)-'0');
            begin++;
        }
        return res*zn;
    }

    public String nextWord() throws IOException {
        return next("word");
    }

    private String next(String type) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!isEnd() && isSymbol(type, buffer[beginInput])) {
            sb.append(buffer[beginInput]);
            updateBuffer();
        }
        return sb.toString();
    }

    private boolean isSymbol(String type, char sym) throws IOException {
        if (type == "number") {
            return CheckerNumber.isValid(sym);
        }
        if (type == "word") {
            return CheckerWord.isValid(sym);
        }
        return false;
    }

    public boolean hasSeparator() throws IOException {
        int ind = beginInput;
        while ((ind < endInput) && separator.length() > ind-beginInput) {
            if (buffer[ind] != separator.charAt(ind-beginInput)) {
                return false;
            }
            ind++;
        }
        lineNumber++;
        beginInput = ind;
        if (beginInput >= endInput) {
            readBuffer();
        }
        return true;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    private void updateBuffer() throws IOException {
        beginInput++;
        if (beginInput >= endInput) {
            readBuffer();
        }
    }

    private void readBuffer() throws IOException {
        beginInput = 0;
        endInput = reader.read(buffer);
    }

    public boolean isEnd() {
        if (endInput == -1) {
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        reader.close();
    }
}

interface CharChecker{
    boolean isValid(char sym);
}

