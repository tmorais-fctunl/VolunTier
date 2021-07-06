package voluntier.exceptions;

public class ImpossibleActionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImpossibleActionException () {
		super();
	}
	
	public ImpossibleActionException (String message) {
		super(message);
	}
}
