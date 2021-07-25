package voluntier.util.consumes.user;

import voluntier.util.consumes.generic.RequestData;

public class RemoveUserData extends RequestData{
	public String target;

	public RemoveUserData() {}
	public RemoveUserData(String email, String token, String target) {
		super(email, token);
		this.target = target;
	}

	public boolean isValid() {	
		return super.isValid() && target != null;
	}
}
