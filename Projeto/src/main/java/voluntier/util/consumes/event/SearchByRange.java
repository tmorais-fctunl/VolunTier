package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;
import voluntier.util.eventdata.EventData_Minimal;

public class SearchByRange extends RequestData {

	public double[] left_limit;
	public double[] right_limit;
	
	public SearchByRange() {
	}
	
	public SearchByRange (String email, String token, double[] left_limit, double[] right_limit) {
		super (email, token);
		this.left_limit = left_limit;
		this.right_limit = right_limit;
	}
	
	public boolean isValid () {
		return super.isValid() && EventData_Minimal.locationValid(left_limit) && EventData_Minimal.locationValid(right_limit);
	}
}
