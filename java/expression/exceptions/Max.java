package expression.exceptions;

import expression.Expressions;
public class Max extends BinaryOperation {

    public Max(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public String getOperator() {
        return ">?";
    }

    @Override
    public int getAnswer(int first, int second) {
        if (first > second) {
            return first;
        }
        return second;
    }

    @Override
    public double getAnswer(double first, double second) {
        if (first > second) {
            return first;
        }
        return second;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean needBracket() {
        return false;
    }

    @Override
    public boolean needBracketEqual() {
        return true;
    }
}