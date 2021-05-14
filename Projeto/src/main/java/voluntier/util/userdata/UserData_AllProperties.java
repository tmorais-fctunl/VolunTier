package voluntier.util.userdata;

import com.google.cloud.datastore.Entity;

import voluntier.util.RegisterData;

public class UserData_AllProperties extends UserData_Modifiable {

	private Roles role;
	private State state;
	private Account account;

	public UserData_AllProperties(){}
	
	public UserData_AllProperties(RegisterData data){
		super(data);
		
		this.setRole(Roles.USER);
		this.setState(State.ENABLED);
		this.setAccount(Account.ACTIVE);
	}
	
	public UserData_AllProperties(Entity user) {
		super(user);
		this.role = Roles.valueOf(user.getString("user_role"));
		this.state = State.valueOf(user.getString("user_state"));
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
