package expression.generic.types;

public interface Type<T> {
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T negate(T a);
    T constParse(String a);
    T max(T a, T b);
    T min(T a, T b);
    T less(T a, T b);
    T greater(T a, T b);
    T lessOrEqual(T a, T b);
    T greaterOrEqual(T a, T b);
    T equal(T a, T b);
    T notEqual(T a, T b);
    T cast(int a);
}
