package fm.pattern.spin.config;

public class SpinConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -8642926441803895231L;

    public SpinConfigurationException(String message) {
        super(message);
    }

    public SpinConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
