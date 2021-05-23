package voluntier.util.userdata;

import com.google.cloud.datastore.Entity;

import com.google.cloud.datastore.Key;

import voluntier.util.consumes.RegisterData;
import voluntier.util.consumes.UpdateProfileData;

public class DB_User {

	public static final String ID = "user_id";
	public static final String PASSWORD = "user_pwd";
	public static final String EMAIL = "user_email";
	public static final String ROLE = "user_role";
	public static final String STATE = "user_state";
	public static final String PROFILE = "user_profile";
	public static final String LANDLINE = "user_landline";
	public static final String MOBILE = "user_mobile";
	public static final String ADDRESS = "user_address";
	public static final String ADDRESS2 = "user_address2";
	public static final String REGION = "user_region";
	public static final String POSTAL_CODE = "user_pc";
	public static final String ACCOUNT = "user_account";
	
	public static Entity changePassword(String new_password, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(ID, user.getString(ID))
				.set(PASSWORD, UserData_Modifiable.hashPassword(new_password))
				.set(EMAIL, user.getString(EMAIL))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.build();
	}
	
	public static Entity remove(Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(ID, user.getString(ID))
				.set(PASSWORD, user.getString(PASSWORD))
				.set(EMAIL, user.getString(EMAIL))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, Account.REMOVED.toString())
				.build();
	}
	
	public static Entity setState(String state, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(ID, user.getString(ID))
				.set(PASSWORD, user.getString(PASSWORD))
				.set(EMAIL, user.getString(EMAIL))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, state)
				.set(PROFILE, user.getString(PROFILE))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.build();
	}
	
	public static Entity changeProperty(UpdateProfileData data, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(ID, user.getString(ID))
				.set(PASSWORD, data.getPassword(user.getString(PASSWORD)))
				.set(EMAIL, data.getEmail(user.getString(EMAIL)))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, data.getProfile(user.getString(PROFILE)))
				.set(LANDLINE, data.getLandline(user.getString(LANDLINE)))
				.set(MOBILE, data.getMobile(user.getString(MOBILE)))
				.set(ADDRESS, data.getAddress(user.getString(ADDRESS)))
				.set(ADDRESS2, data.getAddress2(user.getString(ADDRESS2)))
				.set(REGION, data.getRegion(user.getString(REGION)))
				.set(POSTAL_CODE, data.getPc(user.getString(POSTAL_CODE)))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.build();
	}
	
	public static Entity changeRole(String role, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(ID, user.getString(ID))
				.set(PASSWORD, user.getString(PASSWORD))
				.set(EMAIL, user.getString(EMAIL))
				.set(ROLE, role)
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.build();
	}
	
	public static Entity createNew(String user_id, String email, String password, Key userKey, Entity user) {

		UserData_AllProperties data = new UserData_AllProperties(new RegisterData(user_id, email, password));
		return Entity.newBuilder(userKey)
				.set(ID, data.user_id)
				.set(PASSWORD, data.password)
				.set(EMAIL, data.email)
				.set(ROLE, data.getRole().toString())
				.set(STATE, data.getState().toString())
				.set(PROFILE, data.profile)
				.set(LANDLINE, data.landline)
				.set(MOBILE, data.mobile)
				.set(ADDRESS, data.address)
				.set(ADDRESS2, data.address2)
				.set(REGION, data.region)
				.set(POSTAL_CODE, data.pc)
				.set(ACCOUNT, Account.ACTIVE.toString())
				.build();
	}

}
