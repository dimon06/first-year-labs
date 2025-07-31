package expression.exceptions;

import expression.Expressions;

public class CheckedMultiply extends BinaryOperation{

    public CheckedMultiply(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public int getAnswer(int first, int second) {
        if ((first > 0 && second > 0 && first > Integer.MAX_VALUE / second)
                || (first < 0 && second < 0 && first < Integer.MAX_VALUE / second)
                || (first > 0 && second < 0 && second < Integer.MIN_VALUE / first)
                || (first < 0 && second > 0 && first < Integer.MIN_VALUE / second)) {
            throw new MultiplyOverflowException(first, second);
        }
        return first * second;
    }

    @Override
    public double getAnswer(double first, double second) {
        return first * second;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public boolean needBracket() {
        return false;
    }

    @Override
    public boolean needBracketEqual() {
        return true;
    }

    @Override
    public String getOperator() {
        return "*";
    }
}
