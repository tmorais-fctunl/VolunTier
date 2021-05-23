package voluntier.util.consumes;

import voluntier.util.userdata.State;

public class UpdateStateData extends RequestData{
	public String state;
	
	public UpdateStateData() {}
	public UpdateStateData(String username, String token, String state) {
		super(username, token);
		this.state = state;
	}
	
	public boolean isValid() {	
		return super.isValid() && state != null 
				&& (state.equals(State.BANNED.toString()) 
				|| state.equals(State.ENABLED.toString()));
	}
}