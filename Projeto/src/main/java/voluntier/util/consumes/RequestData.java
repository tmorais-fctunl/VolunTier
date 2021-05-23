package voluntier.util.consumes;

public class RequestData {
	
	public String user_id;
	public String token;
	
	public RequestData() {}
	public RequestData(String username, String token) {
		this.user_id = username;
		this.token = token;
	}
	
	public boolean isValid() {
		return user_id != null && token!= null;
	}
}
