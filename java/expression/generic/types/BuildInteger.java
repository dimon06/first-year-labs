package expression.generic.types;

public class BuildInteger implements ModeType {

    @Override
    public Type<?> build() {
        return new IntegerType();
    }
}
