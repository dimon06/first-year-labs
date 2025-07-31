package expression.generic.expressions;

import java.util.Objects;
import java.util.Set;

public class Variable<T> implements GenericExpression<T> {
    private final String name;
    private final Set<Character> valid = Set.of('x', 'y', 'z');

    public Variable(String name) {
        if (!valid.contains(name.charAt(name.length()-1))) {
            throw new IllegalArgumentException("Unexpected name of variable" + name);
        }
        this.name = name;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch(name.charAt(name.length()-1)) {
            case 'x' -> x;
            case 'y' -> y;
            case 'z' -> z;
            default -> throw new IllegalArgumentException("Unexpected name " + name);
        };
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toMiniString() {
        return this.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Variable other = (Variable) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
