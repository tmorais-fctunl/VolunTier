package voluntier.util.userdata;

public class DonationData {
	public String cause_name;
	public String cause_id;
	public float amount;
	public String timestamp;
	
	public DonationData(String cause_name, String cause_id, float amount, String timestamp) {
		this.cause_name = cause_name;
		this.cause_id = cause_id;
		this.amount = amount;
		this.timestamp = timestamp;
	}
}
