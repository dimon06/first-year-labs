package expression.generic.parser;

import expression.generic.expressions.GenericExpression;

@FunctionalInterface
public interface TripleParser<T> {
    GenericExpression<T> parse(String expression);
}