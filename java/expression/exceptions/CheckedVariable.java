package expression.exceptions;

import expression.Expressions;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CheckedVariable implements Expressions {
    private final String name;
    private final Set<Character> valid = Set.of('x', 'y', 'z');

    public CheckedVariable(String name, int pos) throws VariableException{
        if (!valid.contains(name.charAt(name.length()-1))) {
            throw new VariableException("Unexpected name of variable: " + name, pos);
        }
        this.name = name;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch(name.charAt(name.length()-1)) {
            case 'x' -> x;
            case 'y' -> y;
            case 'z' -> z;
            default -> throw new IllegalArgumentException("Unexpected name: " + name);
        };
    }

    @Override
    public double evaluateD(Map<String, Double> x) {
        var value = x.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Unexpected name: " + name);
        }
        return value;
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
        CheckedVariable other = (CheckedVariable) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
