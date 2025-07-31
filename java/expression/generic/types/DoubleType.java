package expression.generic.types;

import java.util.Objects;

public class DoubleType implements Type<Double> {

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double constParse(String a) {
        return Double.parseDouble(a);
    }

    @Override
    public Double max(Double a, Double b) {
        return Math.max(a, b);
    }

    @Override
    public Double min(Double a, Double b) {
        return Math.min(a, b);
    }

    @Override
    public Double less(Double a, Double b) {
        return a.compareTo(b) < 0 ? 1.0 : 0.0;
    }

    @Override
    public Double greater(Double a, Double b) {
        return a.compareTo(b) > 0 ? 1.0 : 0.0;
    }

    @Override
    public Double lessOrEqual(Double a, Double b) {
        return a.compareTo(b) <= 0 ? 1.0 : 0.0;
    }

    @Override
    public Double greaterOrEqual(Double a, Double b) {
        return a.compareTo(b) >= 0 ? 1.0 : 0.0;
    }

    @Override
    public Double equal(Double a, Double b) {
        return (Objects.equals(a, b)) ? 1.0 : 0.0;
    }

    @Override
    public Double notEqual(Double a, Double b) {
        return (!Objects.equals(a, b)) ? 1.0 : 0.0;
    }

    @Override
    public Double cast(int a) {
        return (double) a;
    }
}
