package exceptions;

public class DiscothequeVideException extends RuntimeException {
    public DiscothequeVideException(String message) {
        super("\u001B[31m" + message + "\u001B[0m");
    }
}
