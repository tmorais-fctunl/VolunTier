package voluntier.util.consumes;

import voluntier.util.userdata.UserData_Modifiable;

public class RegisterData {

	public String user_id;
	public String email;
	public String password;

	public RegisterData() {
	}

	public RegisterData(String user_id, String email, String password) {
		this.user_id = user_id;
		this.email = email;
		this.password = password;
	}

	public boolean isValid() {
		return UserData_Modifiable.idValid(user_id) && UserData_Modifiable.passwordValid(password)
				&& UserData_Modifiable.emailValid(email);
	}
}
