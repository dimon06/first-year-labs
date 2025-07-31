package expression.generic.types;

public class BuildFixedPoint implements ModeType{
    @Override
    public Type<?> build() {
        return new FixedPointType();
    }
}
