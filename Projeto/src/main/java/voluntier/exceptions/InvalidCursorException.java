package voluntier.exceptions;

public class InvalidCursorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCursorException () {
		super();
	}
	
	public InvalidCursorException (String message) {
		super(message);
	}
}
