package voluntier.exceptions;

public class CannotCreateMoreEventsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotCreateMoreEventsException () {
		super();
	}
	
	public CannotCreateMoreEventsException (String message) {
		super(message);
	}
	
}
