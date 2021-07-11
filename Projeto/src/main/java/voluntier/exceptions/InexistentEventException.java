package voluntier.exceptions;

public class InexistentEventException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentEventException() {
        super();
    }
	
	public InexistentEventException (String message) {
		super (message);
	}
	
}
