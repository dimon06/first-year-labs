package expression;

public interface Binary extends Expression {
    int getAnswer(int first, int second);
    double getAnswer(double first, double second);
    int getPriority();
    boolean needBracket();
    boolean needBracketEqual();
    String getOperator();
}
