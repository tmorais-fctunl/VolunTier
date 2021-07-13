package voluntier.exceptions;

public class InexistentPictureException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentPictureException() {
        super();
    }
	
	public InexistentPictureException (String message) {
		super (message);
	}
	
}
