package expression.exceptions;

public class DivideByZeroException extends ArithmeticExceptions {
  public DivideByZeroException(int a, int b) {
    super("Division by zero with argument " + a + " and " + b);
  }
}