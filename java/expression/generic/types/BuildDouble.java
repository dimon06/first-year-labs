package expression.generic.types;

public class BuildDouble implements ModeType{

    @Override
    public Type<?> build() {
        return new DoubleType();
    }
}
