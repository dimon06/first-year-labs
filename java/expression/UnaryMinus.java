package expression;

public class UnaryMinus extends UnaryOperation {

    public UnaryMinus(Expressions value) {
        super(value);
    }

    public int getAnswer(int value) {
        return -value;
    }

    public double getAnswer(double value) {
        return -value;
    }

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
        if (value instanceof BinaryOperation) {
            return "-(" + value.toMiniString() + ")";
        }
        return "- " + value.toMiniString();
    }
}