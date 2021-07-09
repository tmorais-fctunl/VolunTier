package voluntier.util.consumes.event;

public class SearchByRange extends EventData {

	public double[] location;
	
	public SearchByRange (String email, String token, String event_id, double[] location) {
		super (email, token, event_id);
		this.location = location;
	}
	
	public boolean isValid () {
		return super.isValid() && location[0] != -1 && location[1] != -1;
	}
}
