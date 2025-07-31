package expression.generic.exceptions;

public class DivideByZeroException extends ArithmeticException {
  public DivideByZeroException(int a, int b) {
    super("Division by zero with argument " + a + " and " + b);
  }
}