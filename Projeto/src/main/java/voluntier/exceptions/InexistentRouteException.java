package voluntier.exceptions;

public class InexistentRouteException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentRouteException() {
        super();
    }
	
	public InexistentRouteException (String message) {
		super (message);
	}
	
}
