package expression.generic.expressions;

import expression.generic.types.Type;

import java.util.Objects;

public abstract class BinaryOperation<T> implements GenericExpression<T>, Binary<T> {
    private final GenericExpression<T> first, second;

    protected Type<T> type;

    public BinaryOperation(GenericExpression<T> first, GenericExpression<T> second, Type<T> type) {
        this.first = first;
        this.second = second;
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + " " + getOperator() + " " + second.toString() + ")";
    }

    public String toMiniString() {
        StringBuilder sb = new StringBuilder();

        if ((first instanceof expression.exceptions.BinaryOperation) && this.getPriority() > ((expression.exceptions.BinaryOperation) first).getPriority()) {
            sb.append("(").append(first.toMiniString()).append(")");
        } else {
            sb.append(first.toMiniString());
        }

        sb.append(" ").append(getOperator()).append(" ");

        if ((second instanceof expression.exceptions.BinaryOperation operation) && (getPriority() > operation.getPriority()
                || (getPriority() == operation.getPriority() && (needBracket()
                || (needBracketEqual() && this.getClass() != second.getClass()))))) {
            sb.append("(").append(second.toMiniString()).append(")");
        } else {
            sb.append(second.toMiniString());
        }

        return sb.toString();
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return getAnswer(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BinaryOperation other = (BinaryOperation) obj;
        return first.equals(other.first) && second.equals(other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, getClass());
    }
}
