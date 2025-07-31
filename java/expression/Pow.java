package expression;

public class Pow extends BinaryOperation{
    public Pow(Expressions first, Expressions second) {
        super(first, second);
    }

    public int binPower(int a, int p) {
        if (p == 0) {
            return 1;
        }
        int res = binPower(a, p / 2);
        res = res * res;
        if (p % 2 == 1) {
            res = res * a;
        }
        return res;
    }

    public int getAnswer(int first, int second) {
        return binPower(first, second);
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
        return "**";
    }
}
