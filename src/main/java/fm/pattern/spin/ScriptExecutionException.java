package fm.pattern.spin;

public class ScriptExecutionException extends RuntimeException {

    private static final long serialVersionUID = -7642926441803895231L;

    public ScriptExecutionException(String message) {
        super(message);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
