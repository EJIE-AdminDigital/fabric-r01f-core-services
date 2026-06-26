package r01f.core.fileexplorer;

public class InvalidVolumeException extends RuntimeException {
	private static final long serialVersionUID = -5965641886415477416L;

	public InvalidVolumeException(String message) {
		super(message);
	}

	public InvalidVolumeException(String message, Throwable cause) {
		super(message, cause);
	}
}