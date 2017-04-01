package fm.pattern.spin;

public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = -864679264418095231L;

    public TimeoutException(String message) {
        super(message);
    }

}
