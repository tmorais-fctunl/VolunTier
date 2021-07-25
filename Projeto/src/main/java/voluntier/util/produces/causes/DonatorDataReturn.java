package voluntier.util.produces.causes;

public class DonatorDataReturn {
	
	public String pic_64;
	public String username;
	public String email;
	public float donation;
	public String timestamp;
	
	public DonatorDataReturn (String pic_64, String username, String email, float donation, String timestamp) {
		this.pic_64 = pic_64;
		this.username = username;
		this.email = email;
		this.donation = donation;
		this.timestamp = timestamp;
	}
}
