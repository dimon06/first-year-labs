package expression.exceptions;

public class NotExpectedBehavior extends ParsingExceptions {
    public NotExpectedBehavior(String message, int pos) {
        super(message, pos);
    }
}
