package expression.generic.exceptions;

public class DivideOverflowException extends OverflowException {
    public DivideOverflowException(int a, int b) {
        super("divide", a + "/" + b);
    }
}
