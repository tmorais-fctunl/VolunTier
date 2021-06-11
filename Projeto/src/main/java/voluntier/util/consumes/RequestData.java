package voluntier.util.consumes;

public class RequestData {
	
	public String email;
	public String token;
	
	public RequestData() {}
	public RequestData(String email, String token) {
		this.email = email;
		this.token = token;
	}
	
	public boolean isValid() {
		return email != null && token != null && !email.equals("") && !token.equals("");
	}
}
