package expression;

public class Log extends BinaryOperation{
    public Log(Expressions first, Expressions second) {
        super(first, second);
    }

    public int getAnswer(int first, int second) {
        if (first == 1 || first < 0 || second < 0) {
            return 0;
        }
        if (first == 0 && second != 0) {
            return Integer.MIN_VALUE;
        }
        if (second == 1) {
            return Integer.MAX_VALUE;
        }
        if (second == 0) {
            return 0;
        }
        int result = 0;
        int temp = second;
        int over = Integer.MAX_VALUE / second;
        while (temp <= first) {
            result++;
            if (temp > over) {
                break;
            } else {
                temp *= second;
            }
        }
        return result;
    }

    public double getAnswer(double first, double second) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public int getPriority() {
        return 3;
    }

    public boolean needBracket() {
        return true;
    }

    public boolean needBracketEqual() {
        return false;
    }

    public String getOperator() {
        return "//";
    }
}
