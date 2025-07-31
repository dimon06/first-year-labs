package expression.generic.expressions;


import expression.generic.types.Type;

public class Multiply<T> extends BinaryOperation<T> {
    public Multiply(GenericExpression<T> a, GenericExpression<T> b, Type<T> type) {
        super(a, b, type);
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.multiply(first, second);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean needBracket() {
        return false;
    }

    @Override
    public boolean needBracketEqual() {
        return false;
    }

    @Override
    public String getOperator() {
        return "*";
    }
}
