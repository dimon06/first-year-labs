package expression.generic;

import expression.generic.expressions.GenericExpression;
import expression.generic.parser.GenericParser;
import expression.generic.types.*;
import expression.generic.exceptions.ParsingExceptions;

public class GenericTabulator implements Tabulator {

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParsingExceptions {
        return getTabulate(GetType.getType(mode).build(), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] getTabulate(Type<T> type, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParsingExceptions {
        Object[][][] answer = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        GenericParser<T> parser = new GenericParser<>();
        GenericExpression<T> expr = parser.parse(expression, type);
        for (int i = 0; i <= x2 - x1; i++) {
            for (int j = 0; j <= y2 - y1; j++) {
                for (int k = 0; k <= z2 - z1; k++) {
                    try {
                        answer[i][j][k] = expr.evaluate(type.cast((x1 + i)), type.cast((y1 + j)),
                                type.cast((z1 + k)));
                    } catch (ArithmeticException e) {
                        answer[i][j][k] = null;
                    }
                }
            }
        }
        return answer;
    }
}
