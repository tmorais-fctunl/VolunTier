package voluntier.util.eventdata;

public class EventParticipantData {
	
	public String email;
	String username;
	String pic;
	public String role;
	
	public EventParticipantData() {
	}
	
	public EventParticipantData (String email, String username, String pic, String role) {
		this.email = email;
		this.role = role;
		this.pic = pic;
		this.username = username;
	}
}
