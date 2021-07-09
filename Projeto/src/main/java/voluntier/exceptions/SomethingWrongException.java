package voluntier.exceptions;

public class SomethingWrongException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SomethingWrongException () {
		super();
	}
	
	public SomethingWrongException (String message) {
		super(message);
	}
}
