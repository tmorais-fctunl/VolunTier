package voluntier.util.consumes;

import voluntier.util.userdata.UserData_AllProperties;

public class UpdateStateData extends RequestData{
	public String state;
	
	public UpdateStateData() {}
	public UpdateStateData(String email, String token, String state) {
		super(email, token);
		this.state = state;
	}
	
	public boolean isValid() {	
		return super.isValid() && UserData_AllProperties.stateValid(state);
	}
}
