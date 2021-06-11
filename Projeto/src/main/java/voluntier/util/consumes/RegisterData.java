package voluntier.util.consumes;

import voluntier.util.userdata.UserData_Modifiable;

public class RegisterData {

	public String email;
	public String username;
	public String password;

	public RegisterData() {
	}

	public RegisterData(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public boolean isValid() {
		return UserData_Modifiable.passwordValid(password)
				&& UserData_Modifiable.emailValid(email)
		&& UserData_Modifiable.usernameValid(username);
	}
}
