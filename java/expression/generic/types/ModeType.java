package expression.generic.types;

import java.util.function.Supplier;

// :NOTE: то же, что и Supplier<Type<?>>
public interface ModeType {

    Type<?> build();
}
