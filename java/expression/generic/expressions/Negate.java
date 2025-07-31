package expression.generic.expressions;

import expression.generic.types.Type;

public class Negate<T> implements GenericExpression<T> {
    private final GenericExpression<T> value;

    protected Type<T> type;

    public Negate(GenericExpression<T> first, Type<T> type) {
        this.value = first;
        this.type = type;
    }

    public T getAnswer(T value) {
        return type.negate(value);
    }

    public String getOperator() {
        return "-";
    }

    @Override
    public String toString() {
        return "-(" + value.toString() + ")";
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return getAnswer(value.evaluate(x, y, z));
    }

    @Override
    public String toMiniString() {
        if (value instanceof BinaryOperation) {
            return "-(" + value.toMiniString() + ")";
        }
        return "- " + value.toMiniString();
    }
}