package expression.generic.expressions;

import expression.generic.types.Type;

public class Max<T> extends BinaryOperation<T> {

    public Max(GenericExpression<T> first, GenericExpression<T> second, Type<T> type) {
        super(first, second, type);
    }

    @Override
    public String getOperator() {
        return ">?";
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.max(first, second);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean needBracket() {
        return false;
    }

    @Override
    public boolean needBracketEqual() {
        return true;
    }
}