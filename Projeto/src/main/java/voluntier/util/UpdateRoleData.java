package voluntier.util;

import voluntier.util.userdata.Roles;

public class UpdateRoleData extends RequestData{
	public String role;

	public UpdateRoleData() {}
	public UpdateRoleData(String username, String token, String role) {
		super(username, token);
		this.role = role;
	}

	public boolean isValid() {	
		return super.isValid() && role != null 
				&& (role.equals(Roles.USER.toString()) 
						|| role.equals(Roles.GBO.toString())
						|| role.equals(Roles.GA.toString())
						|| role.equals(Roles.SU.toString()));
	}
}
