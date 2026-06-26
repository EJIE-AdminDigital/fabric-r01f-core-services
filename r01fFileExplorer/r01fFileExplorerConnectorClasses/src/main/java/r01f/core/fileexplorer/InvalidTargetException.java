package r01f.core.fileexplorer;

public class InvalidTargetException extends RuntimeException {
	private static final long serialVersionUID = -5965641886415477416L;

	public InvalidTargetException(String message) {
		super(message);
	}

	public InvalidTargetException(String message, Throwable cause) {
		super(message, cause);
	}
}