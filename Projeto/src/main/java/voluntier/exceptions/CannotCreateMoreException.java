package voluntier.exceptions;

public class CannotCreateMoreException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotCreateMoreException () {
		super();
	}
	
	public CannotCreateMoreException (String message) {
		super(message);
	}
	
}
