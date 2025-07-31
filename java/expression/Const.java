package expression;

import java.util.Map;
import java.util.Objects;

public class Const implements Expressions {
    private final Number value;

    public Const(int value) {
        this.value = value;
    }

    public Const(double value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public int evaluate(final int x) {
        return value.intValue();
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return value.intValue();
    }

    @Override
    public double evaluateD(Map<String, Double> x) {
        return value.doubleValue();
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
        return value.intValue();
    }
}
