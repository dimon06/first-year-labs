package expression;

public class Square extends Powers {
    public Square(Expressions value) {
        super(value);
    }

    public int getAnswer(int value) {
        return value * value;
    }

    public double getAnswer(double value) {
        return value * value;
    }

    public String getOperator() {
        return "²";
    }
}
