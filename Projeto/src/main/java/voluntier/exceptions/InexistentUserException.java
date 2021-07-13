package voluntier.exceptions;

public class InexistentUserException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentUserException() {
        super();
    }
	
	public InexistentUserException (String message) {
		super (message);
	}
	
}
