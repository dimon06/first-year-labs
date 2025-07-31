package expression;

import java.util.Map;
import java.util.Objects;

public abstract class BinaryOperation implements Expressions, Binary {
    private final Expressions first, second;

    public BinaryOperation(Expressions first, Expressions second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + " " + getOperator() + " " + second.toString() + ")";
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();

        if ((first instanceof BinaryOperation) && this.getPriority() > ((BinaryOperation) first).getPriority()) {
            sb.append("(").append(first.toMiniString()).append(")");
        } else {
            sb.append(first.toMiniString());
        }

        sb.append(" ").append(getOperator()).append(" ");

        if ((second instanceof BinaryOperation operation) && (getPriority() > operation.getPriority()
                || (getPriority() == operation.getPriority() && needBracket())
                || (this instanceof Multiply && second instanceof Divide))) {
            sb.append("(").append(second.toMiniString()).append(")");
        } else {
            sb.append(second.toMiniString());
        }

        return sb.toString();
    }

    @Override
    public int evaluate(int x) {
        return getAnswer(first.evaluate(x), second.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return getAnswer(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    @Override
    public double evaluateD(Map<String, Double> x) {
        return getAnswer(first.evaluateD(x), second.evaluateD(x));
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
