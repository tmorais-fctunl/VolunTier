package voluntier.util.consumes;

public class LoginData {
	
	public String email;
	public String password;
	
	public LoginData() {}
	public LoginData(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public boolean isValid() {
		return email != null && password != null;
	}
}
