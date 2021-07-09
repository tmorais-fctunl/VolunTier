package voluntier.exceptions;

public class MaximumSizeReachedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MaximumSizeReachedException() {
        super();
    }
	
	public MaximumSizeReachedException (String message) {
		super (message);
	}
	
}
