package expression.generic.expressions;

import expression.generic.types.Type;

public class Add<T> extends BinaryOperation<T> {

    public Add(GenericExpression<T> a, GenericExpression<T> b, Type<T> type) {
        super(a, b, type);
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.add(first, second);
    }


    @Override
    public int getPriority() {
        return 1;
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
        return "+";
    }
}
