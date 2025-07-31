package expression.generic.types;

import java.util.Objects;

public class FixedPointType implements Type<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        return (a + b);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return (a - b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        long x = a, y = b;
        long temp = x*y;
        return (int) (temp >> 16);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        long x = a, y = b;
        long temp = x << 16;
        return (int) (temp/y);
    }

    @Override
    public Integer negate(Integer a) {
        return (-a);
    }

    @Override
    public Integer constParse(String a) {
        return Integer.parseInt(a)*65536;
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
        return (a < b) ? 65536 : 0;
    }

    @Override
    public Integer greater(Integer a, Integer b) {
        return (a > b) ? 65536 : 0;
    }

    @Override
    public Integer lessOrEqual(Integer a, Integer b) {
        return (a <= b) ? 65536 : 0;
    }

    @Override
    public Integer greaterOrEqual(Integer a, Integer b) {
        return (a >= b) ? 65536 : 0;
    }

    @Override
    public Integer equal(Integer a, Integer b) {
        return (Objects.equals(a, b)) ? 65536 : 0;
    }

    @Override
    public Integer notEqual(Integer a, Integer b) {
        return (!Objects.equals(a, b)) ? 65536 : 0;
    }

    @Override
    public Integer cast(int a) {
        return a*65536;
    }
}
