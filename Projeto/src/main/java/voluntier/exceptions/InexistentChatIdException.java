package voluntier.exceptions;

public class InexistentChatIdException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentChatIdException() {
        super();
    }
	
	public InexistentChatIdException (String message) {
		super (message);
	}
	
}
