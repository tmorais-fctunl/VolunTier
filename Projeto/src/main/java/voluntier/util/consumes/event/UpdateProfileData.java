package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;
import voluntier.util.eventdata.EventData_Minimal;

public class UpdateProfileData extends RequestData {

	public String event_name;
	public String profile;
	
	public UpdateProfileData () {
	}
	
	public UpdateProfileData (String email, String token, String event_name, String profile) {
		super(email, token);
		this.event_name = event_name;
		this.profile = profile;
	}
	
	public boolean isValid () {
		return super.isValid() && event_name != null && EventData_Minimal.profileValid(profile);
	}
}
