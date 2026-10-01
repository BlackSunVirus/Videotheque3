package exceptions;

public class AlbumIntrouvableException extends RuntimeException {
    public AlbumIntrouvableException(String message) {
        super("\u001B[31m" + message + "\u001B[0m");
    }
}
