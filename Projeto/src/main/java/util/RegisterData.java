package util;

public class RegisterData {
	
	public String user_id;
	public String email;
	public String password;
	public String confirmation_password;
	
	public RegisterData() {}
	public RegisterData(String user_id, String email, String password, String confirmation_password) {
		this.user_id = user_id;
		this.email = email;
		this.password = password;
		this.confirmation_password = confirmation_password;
	}
	
	public boolean isValid() {
		return user_id.length() > 4 && password.length() >= 8 
				&& password.length() < 20 
				&& confirmation_password.equals(password) 
				&& email.matches(".+@.+[.].+");
	}
}
