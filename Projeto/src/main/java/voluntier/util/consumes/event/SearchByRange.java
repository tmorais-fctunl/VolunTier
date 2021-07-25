package voluntier.util.consumes.event;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.event.EventData_Minimal;

public class SearchByRange extends RequestData {

	public double[] location;
	
	public SearchByRange() {
	}
	
	public SearchByRange (String email, String token, double[] location) {
		super (email, token);
		this.location = location;
	}
	
	public boolean isValid () {
		return super.isValid() && EventData_Minimal.locationValid(location);
	}
}
