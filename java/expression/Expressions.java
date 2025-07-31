package expression;

import java.util.Map;

public interface Expressions extends Expression, DoubleMapExpression, TripleExpression {
    int evaluate(int x);
    int evaluate(int x, int y, int z);
    double evaluateD(Map<String, Double> x);
    boolean equals(Object o);
    int hashCode();
}
