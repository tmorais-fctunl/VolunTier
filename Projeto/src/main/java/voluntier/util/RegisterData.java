package voluntier.util;

public class RegisterData {
	
	public String user_id;
	public String email;
	public String password;
	
	public RegisterData() {}
	public RegisterData(String user_id, String email, String password) {
		this.user_id = user_id;
		this.email = email;
		this.password = password;
	}
	
	public boolean isValid() {
		return user_id.length() > 4 && password.length() >= 8 
				&& password.length() <= 64 
				&& email.matches(".+@.+[.].+");
	}
}
