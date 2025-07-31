package expression.exceptions;

import expression.Expressions;

public class CheckedSubtract extends BinaryOperation{

    public CheckedSubtract(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public int getAnswer(int first, int second) {
        if ((first >= 0 && second < 0 && first > Integer.MAX_VALUE + second)
                || (first < 0 && second > 0 && first < Integer.MIN_VALUE + second)) {
            throw new SubtractOverflowException(first, second);
        }
        return first - second;
    }

    @Override
    public double getAnswer(double first, double second) {
        return first - second;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public boolean needBracket() {
        return true;
    }

    @Override
    public boolean needBracketEqual() {
        return false;
    }

    @Override
    public String getOperator() {
        return "-";
    }
}
