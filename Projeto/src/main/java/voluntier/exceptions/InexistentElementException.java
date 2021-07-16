package voluntier.exceptions;

public class InexistentElementException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentElementException() {
        super();
    }
	
	public InexistentElementException (String message) {
		super (message);
	}
	
}
