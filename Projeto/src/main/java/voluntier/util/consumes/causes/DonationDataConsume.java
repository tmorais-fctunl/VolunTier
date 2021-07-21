package voluntier.util.consumes.causes;

public class DonationDataConsume extends CauseData {
	public Float amount;
	
	public DonationDataConsume() {
	}
	
	public DonationDataConsume(String email, String token, String cause_id, Float amount) {
		super(email, token, cause_id);
		this.amount = amount;
	}
	
	public boolean isValid() {
		return super.isValid() && amount != null;
	}
}
