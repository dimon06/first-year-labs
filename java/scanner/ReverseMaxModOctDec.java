import java.io.*;
import java.util.Arrays;

public class ReverseMaxModOctDec {
    private final static int MOD = 1_000_000_007;
    public static void main(String[] args) {
        try  {
            int[] maxLine = new int[1];
            int[] maxColumn = new int[1];
            int[] lenLines = new int[1];
            int index1 = 0;
            MyScanner scanner = new MyScanner(System.in);
            scanner.setCheckerNumber(sym -> sym == '-' || Character.isDigit(sym) || sym == 'O' || sym == 'o');
            try {
                boolean yk = true;
                while (!scanner.isEnd()) {
                    if (index1 == maxLine.length) {
                        maxLine = Arrays.copyOf(maxLine, maxLine.length*2);
                        lenLines = Arrays.copyOf(lenLines, lenLines.length*2);
                    }
                    int index2 = 0;
                    while (scanner.hasNextInt()) {
                        if (index2 == maxColumn.length) {
                            maxColumn = Arrays.copyOf(maxColumn, maxColumn.length*2);
                        }
                        int val = scanner.nextInt();
                        if (getModVal(val) > getModVal(maxLine[index1])) {
                            maxLine[index1] = val;
                        }
                        if (getModVal(val) > getModVal(maxColumn[index2])) {
                            maxColumn[index2] = val;
                        }
                        index2++;
                    }
                    lenLines[index1] = index2;
                    index1++;
                }
            } finally {
                scanner.close();
            }
            for (int i = 0; i < index1; i++) {
                for (int j = 0; j < lenLines[i]; j++) {
                    int res = maxLine[i];
                    if (getModVal(maxLine[i]) <= getModVal(maxColumn[j])) {
                        res = maxColumn[j];
                    }
                    String ans = Integer.toOctalString(res);
                    System.out.print(Integer.toOctalString(res) + "o" + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Error when writing file: " + e.getMessage());
        }
    }
    public static int getModVal(int val) {
        return ((val%MOD)+MOD)%MOD;
    }
}
