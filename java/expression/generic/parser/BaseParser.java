package expression.generic.parser;

public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    private char ch = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected int getIndex() {
        return source.getIndex();
    }

    protected char getSymbol() {
        return ch;
    }

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean test(final char expected) {

        return ch == expected;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean take(String expected) {
        int begin = source.getIndex();
        char beg = ch;
        for (int i = 0; i < expected.length(); i++) {
            if (!take(expected.charAt(i))) {
                source.setIndex(begin);
                ch = beg;
                return false;
            }
        }
        return true;
    }

    protected void skipWhitespace() {
        while (Character.isWhitespace(ch)) {
            take();
        }
    }

    protected boolean expect(final char expected) {
        if (!take(expected)) {
            return false;
        }
        return true;
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}
