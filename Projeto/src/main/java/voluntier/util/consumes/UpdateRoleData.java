package voluntier.util.consumes;

import voluntier.util.userdata.UserData_AllProperties;

public class UpdateRoleData extends RequestData{
	public String role;
	public String target;

	public UpdateRoleData() {}
	public UpdateRoleData(String email, String token, String role, String target) {
		super(email, token);
		this.role = role;
		this.target = target;
	}

	public boolean isValid() {	
		return super.isValid() && target != null && UserData_AllProperties.roleValid(role);
	}
}
