package expression.exceptions;

import expression.*;

import java.util.Map;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String expression) throws ParsingExceptions{
        return new ExpressionParserSourse(expression).parse();
    }
    private static class ExpressionParserSourse extends BaseParser {

        public Map<Character, Character> Parens = Map.of('(', ')', '{', '}', '[', ']');

        public ExpressionParserSourse(final String sourse) throws ParsingExceptions{

            super(new StringSource(sourse));
        }

        public Expressions parse() throws ParsingExceptions{
            Expressions result = parseFirst();
            skipWhitespace();
            if (!eof()){
                throw new NotExpectedBehavior("there is still data left after the end of parsing", getIndex());
            }
            return result;
        }

        private Expressions parseFirst() throws ParsingExceptions{
            skipWhitespace();
            Expressions result = parseSecond();
            while (true) {
                skipWhitespace();
                if (take("!=")) {
                    skipWhitespace();
                    result = new NotEqual(result, parseSecond());
                } else if (take("==")) {
                    skipWhitespace();
                    result = new Equal(result, parseSecond());
                } else {
                    return result;
                }
            }
        }

        private Expressions parseSecond() throws ParsingExceptions{
            skipWhitespace();
            Expressions result = parseThird();
            while (true) {
                skipWhitespace();
                if (take("<=")) {
                    skipWhitespace();
                    result = new LessOrEqual(result, parseThird());
                } else if (take(">=")) {
                    skipWhitespace();
                    result = new GreaterOrEqual(result, parseThird());
                } else if (take('<')) {
                    skipWhitespace();
                    result = new Less(result, parseThird());
                } else if (take('>')) {
                    skipWhitespace();
                    result = new Greater(result, parseThird());
                } else {
                    return result;
                }
            }
        }

        private Expressions parseThird() throws ParsingExceptions{
            skipWhitespace();
            Expressions result = parseFourth();
            while (true) {
                skipWhitespace();
                if (take("<?")) {
                    skipWhitespace();
                    result = new Min(result, parseFourth());
                } else if (take(">?")) {
                    skipWhitespace();
                    result = new Max(result, parseFourth());
                } else {
                    return result;
                }
            }
        }

        private Expressions parseFourth() throws ParsingExceptions{
            skipWhitespace();
            Expressions result = parseFifth();
            while (true) {
                skipWhitespace();
                if (take('+')) {
                    skipWhitespace();
                    result = new CheckedAdd(result, parseFifth());
                } else if (take('-')) {
                    skipWhitespace();
                    result = new CheckedSubtract(result, parseFifth());
                } else {
                    return result;
                }
            }
        }

        private Expressions parseFifth() throws ParsingExceptions{
            skipWhitespace();
            Expressions result = parseOther();
            while (true) {
                skipWhitespace();
                if (take('*')) {
                    skipWhitespace();
                    result = new CheckedMultiply(result, parseOther());
                } else if (take('/')) {
                    skipWhitespace();
                    result = new CheckedDivide(result, parseOther());
                } else {
                    return result;
                }
            }
        }

        private Expressions parseOther() throws ParsingExceptions{
            skipWhitespace();
            Expressions result;
            char paren = '0';
            if (Parens.containsKey(getSymbol())) {
               paren = getSymbol();
            }
            if (paren != '0') {
                take();
                skipWhitespace();
                result = parseFirst();
                skipWhitespace();
                expect(Parens.get(paren));
            } else if (between('0', '9')) {
                result = parseConst(true);
            } else if (between('a', 'z') || between('A', 'Z')) {
                result = parseVariable();
            } else if (take('-')) {
                if (between('0', '9')) {
                    result =  parseConst(false);
                } else {
                    result = new CheckedNegate(parseOther());
                }
            } else {
                throw new CanNotParseException("incorrect sequence of operands", getIndex());
            }
            while (true) {
                skipWhitespace();
                if (take('²')) {
                    result = new Square(result);
                } else if (take('³')) {
                    result = new Cube(result);
                } else {
                    break;
                }
            }
            return result;
        }

        private Expressions parseConst(boolean sign) throws ParsingExceptions{
            StringBuilder sb = new StringBuilder();
            if (!sign) {
                sb.append('-');
            }
            while (between('0', '9')) {
                sb.append(getSymbol());
                take();
            }
            skipWhitespace();
            if (between('0', '9')) {
                throw new NotANumberException("spase in number", getIndex());
            }
            int number = 0;
            try {
                number = Integer.parseInt(sb.toString());
            } catch (NumberFormatException e) {
                throw new NumberLengthException("Number out of int range", getIndex());
            }
            return new Const(number);
        }

        private Expressions parseVariable() throws VariableException {
            StringBuilder sb = new StringBuilder();
            while (between('a', 'z') || between('A', 'Z')) {
                sb.append(getSymbol());
                take();
            }
            return new CheckedVariable(sb.toString(), getIndex());
        }
    }
}