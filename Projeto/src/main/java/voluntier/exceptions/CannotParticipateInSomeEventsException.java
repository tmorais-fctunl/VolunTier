package voluntier.exceptions;

public class CannotParticipateInSomeEventsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotParticipateInSomeEventsException () {
		super();
	}
	
	public CannotParticipateInSomeEventsException (String message) {
		super(message);
	}
}
