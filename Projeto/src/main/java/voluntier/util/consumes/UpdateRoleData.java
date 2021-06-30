package voluntier.util.consumes;

import voluntier.util.userdata.UserData_AllProperties;

public class UpdateRoleData extends RequestData{
	public String role;

	public UpdateRoleData() {}
	public UpdateRoleData(String email, String token, String role) {
		super(email, token);
		this.role = role;
	}

	public boolean isValid() {	
		return super.isValid() && UserData_AllProperties.roleValid(role);
	}
}
