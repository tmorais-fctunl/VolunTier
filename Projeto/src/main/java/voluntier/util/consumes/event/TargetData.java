package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class TargetData extends RequestData{
	public String target;

	public TargetData() {}
	public TargetData(String email, String token, String target) {
		super(email, token);
		this.target = target;
	}

	public boolean isValid() {	
		return super.isValid() && target != null;
	}
}
