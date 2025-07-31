package expression.generic.expressions;


public interface Binary<T>  {
    T getAnswer(T first, T second);
    int getPriority();
    boolean needBracket();
    boolean needBracketEqual();
    String getOperator();
}
