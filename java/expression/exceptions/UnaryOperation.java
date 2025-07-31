package expression.exceptions;

import expression.Expressions;

import java.util.Map;
import java.util.Objects;

public abstract class UnaryOperation implements Expressions {
    protected final Expressions value;
    public UnaryOperation(Expressions value) {
        this.value = value;
    }

    protected abstract int getAnswer(int value);

    protected abstract double getAnswer(double value);

    protected abstract String getOperator();

    @Override
    public int evaluate(int x) {
        return getAnswer(value.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return getAnswer(value.evaluate(x, y, z));
    }

    @Override
    public double evaluateD(Map<String, Double> x) {
        return getAnswer(value.evaluateD(x));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        UnaryOperation other = (UnaryOperation) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, getClass());
    }
}
