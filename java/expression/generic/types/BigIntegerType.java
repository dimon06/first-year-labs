package expression.generic.types;

import java.math.BigInteger;

public class BigIntegerType implements Type<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        return a.divide(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger constParse(String a) {
        return new BigInteger(a);
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        return a.max(b);
    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        return a.min(b);
    }

    @Override
    public BigInteger less(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) < 0) ? 1 : 0);
    }

    @Override
    public BigInteger greater(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) > 0) ? 1 : 0);
    }

    @Override
    public BigInteger lessOrEqual(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) <= 0) ? 1 : 0);
    }

    @Override
    public BigInteger greaterOrEqual(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) >= 0) ? 1 : 0);
    }

    @Override
    public BigInteger equal(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) == 0) ? 1 : 0);
    }

    @Override
    public BigInteger notEqual(BigInteger a, BigInteger b) {
        return BigInteger.valueOf((a.compareTo(b) != 0) ? 1 : 0);
    }

    @Override
    public BigInteger cast(int a) {
        return new BigInteger(String.valueOf(a));
    }
}
