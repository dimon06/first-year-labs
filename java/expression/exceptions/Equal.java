package expression.exceptions;

import expression.Expressions;
public class Equal extends BinaryOperation {

    public Equal(Expressions first, Expressions second) {
        super(first, second);
    }

    @Override
    public String getOperator() {
        return "==";
    }

    @Override
    public int getAnswer(int first, int second) {
        if (first == second) {
            return 1;
        }
        return 0;
    }

    @Override
    public double getAnswer(double first, double second) {
        if (first == second) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean needBracket() {
        return true;
    }

    @Override
    public boolean needBracketEqual() {
        return false;
    }
}