package exeptions;

public class ManagerCheckTimeException extends RuntimeException {
    public ManagerCheckTimeException(String message) {
        super(message);
    }
}
