package expression.generic.exceptions;

public class MultiplyOverflowException extends OverflowException {
  public MultiplyOverflowException(int a, int b) {
    super("multiply", a + "*" + b);
  }
}
