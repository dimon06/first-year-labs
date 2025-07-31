package expression.generic.exceptions;

public class UnaryMinusOverflowException extends OverflowException {
  public UnaryMinusOverflowException(int a) {
    super("unary minus " + a);
  }
}
