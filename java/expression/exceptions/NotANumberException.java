package expression.exceptions;

public class NotANumberException extends ParsingExceptions {
    public NotANumberException(String message, int pos) {
        super(message, pos);
    }
}
