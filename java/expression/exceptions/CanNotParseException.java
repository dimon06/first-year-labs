package expression.exceptions;

public class CanNotParseException extends ParsingExceptions {
    public CanNotParseException(String message, int pos) {
        super(message, pos);
    }
}
