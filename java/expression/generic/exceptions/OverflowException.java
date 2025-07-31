package expression.generic.exceptions;

public class OverflowException extends ArithmeticException {
    public OverflowException(String operation, String args) {
        super("Overflow in " + operation + " with argument " + args);
    }
    public OverflowException(String args) {
        super("Overflow with argument " + args);
    }
}
