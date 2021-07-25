package voluntier.util.data.event;

public class ParticipantDataReturn {
	
	public String email;
	String username;
	String pic;
	public String role;
	
	public ParticipantDataReturn() {
	}
	
	public ParticipantDataReturn (String email, String username, String pic, String role) {
		this.email = email;
		this.role = role;
		this.pic = pic;
		this.username = username;
	}
}
