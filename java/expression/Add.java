package expression;

public class Add extends BinaryOperation {

    public Add(Expressions first, Expressions second) {
        super(first, second);
    }

    public int getAnswer(int first, int second) {
        return first + second;
    }

    public double getAnswer(double first, double second) {
        return first + second;
    }

    public int getPriority() {
        return 1;
    }

    public boolean needBracket() {
        return false;
    }

    public boolean needBracketEqual() {
        return false;
    }

    public String getOperator() {
        return "+";
    }
}