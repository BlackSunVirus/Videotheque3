package exceptions;

public class FichierAudioException extends RuntimeException {
    public FichierAudioException(String message) {
        super("\u001B[31m" + message + "\u001B[0m");
    }
}
