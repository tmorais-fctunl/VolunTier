package voluntier.util.consumes.user;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.user.UserData_AllProperties;

public class UpdateStateData extends RequestData{
	public String state;
	public String target;
	
	public UpdateStateData() {}
	public UpdateStateData(String email, String token, String state, String target) {
		super(email, token);
		this.state = state;
		this.target = target;
	}
	
	public boolean isValid() {	
		return super.isValid() && target != null && UserData_AllProperties.stateValid(state);
	}
}
