package expression.generic.exceptions;

public class SubtractOverflowException extends OverflowException {
    public SubtractOverflowException(int a, int b) {
        super("subtract", a + "-" + b);
    }
}
