package expression.generic.types;

public class GetType {
    public static ModeType getType(String name) {
        return switch (name) {
            case "i" -> IntegerType::new;
            case "d" -> new BuildDouble();
            case "bi" -> new BuildBigInteger();
            case "u" -> new BuildUnchekedInteger();
            case "ifix" -> new BuildFixedPoint();
            default -> throw new IllegalArgumentException("Unknown type: " + name);
        };
    }
}
