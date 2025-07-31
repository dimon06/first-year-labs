package expression.exceptions;

public class ParsingExceptions extends Exception {
    public ParsingExceptions(String message, int position) {
        super(message + " in position: " + position);
    }
}
