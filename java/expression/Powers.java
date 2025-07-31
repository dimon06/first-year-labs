package expression;

public abstract class Powers extends UnaryOperation {
    public Powers(Expressions value) {
        super(value);
    }
    @Override
    public String toString() {
        return "(" + value.toString() + ")" + getOperator();
    }

    @Override
    public String toMiniString() {
        if (value instanceof BinaryOperation || value instanceof UnaryMinus) {
            return "(" + value.toMiniString() + ")" + getOperator();
        }
        return value.toMiniString() + getOperator();
    }
}
