package expression.exceptions;

import expression.Expressions;

public class CheckedDivide extends BinaryOperation{
    public CheckedDivide(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public int getAnswer(int first, int second) {
        if (second == 0) {
            throw new DivideByZeroException(first, second);
        }
        if (first == Integer.MIN_VALUE && second == -1) {
            throw new DivideOverflowException(first, second);
        }
        return first / second;
    }

    @Override
    public double getAnswer(double first, double second) {
        return first / second;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public boolean needBracket() {
        return true;
    }

    @Override
    public String getOperator() {
        return "/";
    }

    @Override
    public boolean needBracketEqual() {
        return true;
    }
}
