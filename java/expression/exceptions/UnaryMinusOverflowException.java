package expression.exceptions;

public class UnaryMinusOverflowException extends RuntimeException {
  public UnaryMinusOverflowException(int a) {
    super("unary minus " + a);
  }
}
