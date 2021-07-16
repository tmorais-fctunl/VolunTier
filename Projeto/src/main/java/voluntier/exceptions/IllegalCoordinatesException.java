package voluntier.exceptions;

public class IllegalCoordinatesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalCoordinatesException () {
		super();
	}
	
	public IllegalCoordinatesException (String message) {
		super(message);
	}
}
