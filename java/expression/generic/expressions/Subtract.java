package expression.generic.expressions;

import expression.generic.types.Type;

public class Subtract<T> extends BinaryOperation<T> {
    public Subtract(GenericExpression<T> a, GenericExpression<T> b, Type<T> type) {
        super(a, b, type);
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.subtract(first, second);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean needBracket() {
        return true;
    }

    @Override
    public boolean needBracketEqual() {
        return false;
    }

    @Override
    public String getOperator() {
        return "-";
    }


}
