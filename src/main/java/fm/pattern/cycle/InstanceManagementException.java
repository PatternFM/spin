package fm.pattern.cycle;

public class InstanceManagementException extends RuntimeException {

    private static final long serialVersionUID = -7642926441803895231L;

    public InstanceManagementException(String message) {
        super(message);
    }

    public InstanceManagementException(String message, Throwable cause) {
        super(message, cause);
    }

}
