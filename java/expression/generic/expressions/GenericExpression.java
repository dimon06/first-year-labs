package expression.generic.expressions;

public interface GenericExpression<T> {
    T evaluate(T a, T b, T c);
    String toMiniString();
}
