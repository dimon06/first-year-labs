package expression.generic.expressions;

import expression.generic.types.Type;

public class LessOrEqual<T> extends BinaryOperation<T> {

    public LessOrEqual(GenericExpression<T> first, GenericExpression<T> second, Type<T> type) {
        super(first, second, type);
    }

    @Override
    public String getOperator() {
        return "<=";
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.lessOrEqual(first, second);
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
        return false;
    }
}