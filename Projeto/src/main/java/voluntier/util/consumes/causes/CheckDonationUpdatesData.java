package voluntier.util.consumes.causes;

public class CheckDonationUpdatesData extends CauseData {
	public Long time_millis;
	
	public CheckDonationUpdatesData() {
	}
	
	public CheckDonationUpdatesData(String email, String token, String cause_id, Long time_millis) {
		super(email, token, cause_id);
		this.time_millis = time_millis;
	}
	
	public boolean isValid() {
		return super.isValid() && time_millis != null;
	}
}
