package voluntier.exceptions;

public class InexistentModeratorException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentModeratorException() {
        super();
    }
	
	public InexistentModeratorException (String message) {
		super (message);
	}
	
}
