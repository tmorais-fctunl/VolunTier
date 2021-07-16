package voluntier.exceptions;

public class RouteAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RouteAlreadyExistsException () {
		super();
	}
	
	public RouteAlreadyExistsException (String message) {
		super(message);
	}
}
