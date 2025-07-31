package expression.generic.parser;

import expression.generic.expressions.*;
import expression.generic.types.Type;
import expression.generic.exceptions.*;

import java.util.Map;

public class GenericParser<T> {

    public GenericExpression<T> parse(String expression, Type<T> type) throws ParsingExceptions {
        return new GenericParserSourse<T>(expression).parse(type);
    }
    private static class GenericParserSourse<T> extends BaseParser {

        Type<T>type;

        public Map<Character, Character> Parens = Map.of('(', ')', '{', '}', '[', ']');

        public GenericParserSourse(final String sourse) {

            super(new StringSource(sourse));
        }

        public GenericExpression<T> parse(Type<T> type) throws ParsingExceptions{
            this.type = type;
            GenericExpression<T> result = parseFirst();
            skipWhitespace();
            if (!eof()){
                throw new NotExpectedBehavior("there is still data left after the end of parsing", getIndex());
            }
            return result;
        }

        private GenericExpression<T> parseFirst() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result = parseSecond();
            while (true) {
                skipWhitespace();
                if (take("!=")) {
                    skipWhitespace();
                    result = new NotEqual<>(result, parseSecond(), type);
                } else if (take("==")) {
                    skipWhitespace();
                    result = new Equal<>(result, parseSecond(), type);
                } else {
                    return result;
                }
            }
        }

        private GenericExpression<T> parseSecond() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result = parseThird();
            while (true) {
                skipWhitespace();
                if (take("<=")) {
                    skipWhitespace();
                    result = new LessOrEqual<>(result, parseThird(), type);
                } else if (take(">=")) {
                    skipWhitespace();
                    result = new GreaterOrEqual<>(result, parseThird(), type);
                } else if (take('<')) {
                    skipWhitespace();
                    result = new Less<>(result, parseThird(), type);
                } else if (take('>')) {
                    skipWhitespace();
                    result = new Greater<>(result, parseThird(), type);
                } else {
                    return result;
                }
            }
        }

        private GenericExpression<T> parseThird() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result = parseFourth();
            while (true) {
                skipWhitespace();
                if (take("<?")) {
                    skipWhitespace();
                    result = new Min<>(result, parseFourth(), type);
                } else if (take(">?")) {
                    skipWhitespace();
                    result = new Max<>(result, parseFourth(), type);
                } else {
                    return result;
                }
            }
        }

        private GenericExpression<T> parseFourth() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result = parseFifth();
            while (true) {
                skipWhitespace();
                if (take('+')) {
                    skipWhitespace();
                    result = new Add<>(result, parseFifth(), type);
                } else if (take('-')) {
                    skipWhitespace();
                    result = new Subtract<>(result, parseFifth(), type);
                } else {
                    return result;
                }
            }
        }

        private GenericExpression<T> parseFifth() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result = parseOther();
            while (true) {
                skipWhitespace();
                if (take('*')) {
                    skipWhitespace();
                    result = new Multiply<>(result, parseOther(), type);
                } else if (take('/')) {
                    skipWhitespace();
                    result = new Divide<>(result, parseOther(), type);
                } else {
                    return result;
                }
            }
        }

        private GenericExpression<T> parseOther() throws ParsingExceptions {
            skipWhitespace();
            GenericExpression<T> result;
            char paren = '0';
            if (Parens.containsKey(getSymbol())) {
               paren = getSymbol();
            }
            if (paren != '0') {
                take();
                skipWhitespace();
                result = parseFirst();
                skipWhitespace();
                if (!expect(Parens.get(paren))) {
                    throw new CanNotParseException("incorrect sequence of operands", getIndex());
                }
            } else if (between('0', '9')) {
                result = parseConst(true);
            } else if (between('a', 'z') || between('A', 'Z')) {
                result = parseVariable();
            } else if (take('-')) {
                if (between('0', '9')) {
                    result =  parseConst(false);
                } else {
                    result = new Negate<>(parseOther(), type);
                }
            } else {
                throw new CanNotParseException("incorrect sequence of operands", getIndex());
            }
            return result;
        }

        private GenericExpression<T> parseConst(boolean sign) {
            StringBuilder sb = new StringBuilder();
            if (!sign) {
                sb.append('-');
            }
            while (between('0', '9')) {
                sb.append(getSymbol());
                take();
            }
            return new Const<>(type.constParse(sb.toString()));
        }

        private GenericExpression<T> parseVariable() {
            StringBuilder sb = new StringBuilder();
            while (between('a', 'z') || between('A', 'Z')) {
                sb.append(getSymbol());
                take();
            }
            return new Variable<>(sb.toString());
        }
    }
}