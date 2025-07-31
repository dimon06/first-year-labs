package expression.generic.types;

public class BuildBigInteger implements ModeType{

    @Override
    public Type<?> build() {
        return new BigIntegerType();
    }
}
