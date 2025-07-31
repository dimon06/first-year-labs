package expression.generic.types;

import expression.generic.exceptions.*;

import java.util.Objects;

public class IntegerType implements Type<Integer> {

    @Override
    public Integer add(Integer a, Integer b) {
        if ((a > 0 && b > 0 && a + b <= 0)
                || (a < 0 && b < 0 && a + b >= 0)) {
            throw new AddOverflowException(a, b);
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if ((a >= 0 && b < 0 && a > Integer.MAX_VALUE + b)
                || (a < 0 && b > 0 && a < Integer.MIN_VALUE + b)) {
            throw new SubtractOverflowException(a, b);
        }
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if ((a > 0 && b > 0 && a > Integer.MAX_VALUE / b)
                || (a < 0 && b < 0 && a < Integer.MAX_VALUE / b)
                || (a > 0 && b < 0 && b < Integer.MIN_VALUE / a)
                || (a < 0 && b > 0 && a < Integer.MIN_VALUE / b)) {
            throw new MultiplyOverflowException(a, b);
        }
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivideByZeroException(a, b);
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new DivideOverflowException(a, b);
        }
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new UnaryMinusOverflowException(a);
        }
        return -a;
    }

    @Override
    public Integer constParse(String a) {
        return Integer.parseInt(a);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @Override
    public Integer less(Integer a, Integer b) {
        return (a < b) ? 1 : 0;
    }

    @Override
    public Integer greater(Integer a, Integer b) {
        return (a > b) ? 1 : 0;
    }

    @Override
    public Integer lessOrEqual(Integer a, Integer b) {
        return (a <= b) ? 1 : 0;
    }

    @Override
    public Integer greaterOrEqual(Integer a, Integer b) {
        return (a >= b) ? 1 : 0;
    }

    @Override
    public Integer equal(Integer a, Integer b) {
        return (Objects.equals(a, b)) ? 1 : 0;
    }

    @Override
    public Integer notEqual(Integer a, Integer b) {
        return (!Objects.equals(a, b)) ? 1 : 0;
    }

    @Override
    public Integer cast(int a) {
        return a;
    }
}
