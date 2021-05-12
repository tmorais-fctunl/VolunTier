package voluntier.util;

import com.google.cloud.datastore.Entity;

public class UserDataFull extends UserData {

	private Roles role;
	private State state;
	private Account account;

	public UserDataFull(){}
	
	public UserDataFull(RegisterData data){
		super(data);
		
		this.setRole(Roles.USER);
		this.setState(State.ENABLED);
		this.setAccount(Account.ACTIVE);
	}
	
	public UserDataFull(Entity user) {
		this.user_id = user.getString("user_id");
		this.password = user.getString("user_pwd");
		this.email = user.getString("user_email");
		this.role = Roles.valueOf(user.getString("user_role"));
		this.state = State.valueOf(user.getString("user_state"));
		this.profile = user.getString("user_profile");
		this.landline = user.getString("user_landline");
		this.mobile = user.getString("user_mobile");
		this.address = user.getString("user_address");
		this.address2 = user.getString("user_address2");
		this.region = user.getString("user_region");
		this.pc = user.getString("user_pc");
		this.account = Account.valueOf(user.getString("user_account"));
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	boolean isValid() {
		return super.isValid();
	}

}
