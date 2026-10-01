package exceptions;

public class AlbumDejaExistantException extends RuntimeException {
    public AlbumDejaExistantException(String message) {
        super("\u001B[31m" + message + "\u001B[0m");
    }
}
