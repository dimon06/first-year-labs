package expression.generic.types;

public class BuildUnchekedInteger implements ModeType{
    @Override
    public Type<?> build() {
        return new UnchekedIntegerType();
    }
}
