package voluntier.util.data.user;

import com.google.cloud.datastore.Entity;

import voluntier.util.consumes.user.RegisterData;

public class UserData_AllProperties extends UserData_Modifiable {

	private Roles role;
	private State state;
	private Account account;

	public UserData_AllProperties() {
	}

	public UserData_AllProperties(RegisterData data) {
		super(data);

		this.setRole(Roles.USER);
		this.setState(State.ENABLED);
		this.setAccount(Account.ACTIVE);
	}

	public UserData_AllProperties(Entity user) {
		super(user);
		this.role = Roles.valueOf(user.getString(DB_User.ROLE));
		this.state = State.valueOf(user.getString(DB_User.STATE));
		this.account = Account.valueOf(user.getString(DB_User.ACCOUNT));
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

	public static boolean roleValid(String role) {
		return role != null && (role.equals(Roles.USER.toString()) || role.equals(Roles.GBO.toString())
				|| role.equals(Roles.GA.toString()) || role.equals(Roles.SU.toString()));
	}

	public static boolean stateValid(String state) {
		return state != null && (state.equals(State.BANNED.toString()) || state.equals(State.ENABLED.toString()));
	}

	public static boolean accountValid(String account) {
		return account != null
				&& (account.equals(Account.ACTIVE.toString()) || account.equals(Account.REMOVED.toString()));
	}

	boolean isValid() {
		return super.isValid() && roleValid(role.toString()) && stateValid(state.toString())
				&& accountValid(account.toString());
	}

}
