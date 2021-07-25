package voluntier.util.consumes.user;

import voluntier.util.consumes.generic.RequestData;

public class LookUpData extends RequestData{
	public String target;

	public LookUpData() {}
	public LookUpData(String email, String token, String target) {
		super(email, token);
		this.target = target;
	}

	public boolean isValid() {	
		return super.isValid() && target != null;
	}
}
