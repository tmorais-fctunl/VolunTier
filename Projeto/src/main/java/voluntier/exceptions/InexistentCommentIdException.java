package voluntier.exceptions;

public class InexistentCommentIdException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentCommentIdException() {
        super();
    }
	
	public InexistentCommentIdException (String message) {
		super (message);
	}
	
}
