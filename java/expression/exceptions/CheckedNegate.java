package expression.exceptions;

import expression.Const;
import expression.Expressions;
import expression.UnaryMinus;

public class CheckedNegate extends UnaryOperation {

    public CheckedNegate(Expressions value) {
        super(value);
    }

    @Override
    public int getAnswer(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new UnaryMinusOverflowException(value);
        }
        return -value;
    }

    @Override
    public double getAnswer(double value) {
        return -value;
    }

    @Override
    public String getOperator() {
        return "-";
    }

    public static Expressions build(Expressions expression) {
        if (expression instanceof Const) {
            return new Const(-(expression).evaluate(0));
        }
        return new UnaryMinus(expression);
    }

    @Override
    public String toString() {
        return "-(" + value.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (BinaryOperation.class.isAssignableFrom(value.getClass())) {
            return "-(" + value.toMiniString() + ")";
        }
        return "- " + value.toMiniString();
    }
}
