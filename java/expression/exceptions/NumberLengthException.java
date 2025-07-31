package expression.exceptions;

public class NumberLengthException extends ParsingExceptions {
    public NumberLengthException(String message, int pos) {
        super(message, pos);
    }
}
