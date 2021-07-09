package voluntier.exceptions;

public class InexistentMessageIdException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentMessageIdException() {
        super();
    }
	
	public InexistentMessageIdException (String message) {
		super (message);
	}
	
}
