package voluntier.exceptions;

public class InexistentLogIdException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentLogIdException() {
        super();
    }
	
	public InexistentLogIdException (String message) {
		super (message);
	}
	
}
