package expression.exceptions;

public class NotExpectedSymbol extends ParsingExceptions {
    public NotExpectedSymbol(String message, int pos) {
        super(message, pos);
    }
}
