package expression.generic.expressions;

import expression.generic.types.Type;

public class Divide<T> extends BinaryOperation<T> {
    public Divide(GenericExpression<T> a, GenericExpression<T> b, Type<T> type) {
        super(a, b, type);
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.divide(first, second);
    }


    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean needBracket() {
        return true;
    }

    @Override
    public boolean needBracketEqual() {
        return true;
    }

    @Override
    public String getOperator() {
        return "/";
    }
}
