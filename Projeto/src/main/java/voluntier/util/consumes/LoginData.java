package voluntier.util.consumes;

public class LoginData {
	
	public String user;
	public String password;
	
	public LoginData() {}
	public LoginData(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	public boolean isValid() {
		return user != null && password != null;
	}
}
