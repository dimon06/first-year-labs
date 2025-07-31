package expression;

public class Cube extends Powers {
    public Cube(Expressions value) {
        super(value);
    }

    public int getAnswer(int value) {
        return value * value * value;
    }

    public double getAnswer(double value) {
        return value * value * value;
    }

    public String getOperator() {
        return "³";
    }
}
