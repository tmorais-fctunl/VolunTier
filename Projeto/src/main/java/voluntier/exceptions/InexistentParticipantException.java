package voluntier.exceptions;

public class InexistentParticipantException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InexistentParticipantException() {
        super();
    }
	
	public InexistentParticipantException (String message) {
		super (message);
	}
	
}
