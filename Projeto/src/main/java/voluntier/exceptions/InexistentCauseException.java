package voluntier.exceptions;

public class InexistentCauseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentCauseException () {
		super();
	}
	
	public InexistentCauseException (String message) {
		super(message);
	}
}
