package expression.generic.expressions;

import java.util.Objects;

public class Const<T> implements GenericExpression<T> {
    private final T value;

    public Const(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toMiniString() {
        return toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Const other = (Const) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
