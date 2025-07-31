package expression.exceptions;

public class OverflowException extends ArithmeticExceptions {
    public OverflowException(String operation, String args) {
        super("Overflow in " + operation + " with argument " + args);
    }
}
