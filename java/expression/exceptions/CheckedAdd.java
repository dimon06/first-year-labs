package expression.exceptions;

import expression.Expressions;
public class CheckedAdd extends BinaryOperation {

    public CheckedAdd(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public String getOperator() {
        return "+";
    }

    @Override
    public int getAnswer(int first, int second) {
        if ((first > 0 && second > 0 && first + second <= 0)
                || (first < 0 && second < 0 && first + second >= 0)) {
            throw new AddOverflowException(first, second);
        }
        return first + second;
    }

    @Override
    public double getAnswer(double first, double second) {
        return first + second;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public boolean needBracket() {
        return false;
    }

    @Override
    public boolean needBracketEqual() {
        return false;
    }
}