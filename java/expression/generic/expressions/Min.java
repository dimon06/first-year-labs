package expression.generic.expressions;

import expression.generic.types.Type;

public class Min<T> extends BinaryOperation<T> {

    public Min(GenericExpression<T> first, GenericExpression<T> second, Type<T> type) {
        super(first, second, type);
    }

    @Override
    public String getOperator() {
        return "<?";
    }

    @Override
    public T getAnswer(T first, T second) {
        return type.min(first, second);
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