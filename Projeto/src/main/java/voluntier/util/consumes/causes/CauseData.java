package voluntier.util.consumes.causes;

import voluntier.util.consumes.RequestData;

public class CauseData extends RequestData {
	public String cause_id;
	
	public CauseData() {
	}
	
	public CauseData(String email, String token, String cause_id) {
		super(email, token);
		this.cause_id = cause_id;
	}
	
	public boolean isValid() {
		return super.isValid() && cause_id != null && !cause_id.equals("");
	}
}
