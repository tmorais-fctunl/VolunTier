package voluntier.util.consumes;

public class LoginData {
	
	public String user_id;
	public String password;
	
	public LoginData() {}
	public LoginData(String username, String password) {
		this.user_id = username;
		this.password = password;
	}
	
	public boolean isValid() {
		return user_id != null && password != null;
	}

}
