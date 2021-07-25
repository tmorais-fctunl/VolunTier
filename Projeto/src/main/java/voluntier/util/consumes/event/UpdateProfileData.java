package voluntier.util.consumes.event;

import voluntier.util.data.event.EventData_Minimal;

public class UpdateProfileData extends EventData {

	public String profile;
	
	public UpdateProfileData () {
	}
	
	public UpdateProfileData (String email, String token, String event_id, String profile) {
		super(email, token, event_id);
		this.profile = profile;
	}
	
	public boolean isValid () {
		return super.isValid() && EventData_Minimal.profileValid(profile);
	}
}
