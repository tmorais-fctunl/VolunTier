package voluntier.exceptions;

public class InexistentFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentFileException () {
		super();
	}
	
	public InexistentFileException (String message) {
		super(message);
	}
}
