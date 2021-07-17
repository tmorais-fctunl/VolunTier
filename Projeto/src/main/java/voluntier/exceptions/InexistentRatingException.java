package voluntier.exceptions;

public class InexistentRatingException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentRatingException() {
        super();
    }
	
	public InexistentRatingException (String message) {
		super (message);
	}
	
}
