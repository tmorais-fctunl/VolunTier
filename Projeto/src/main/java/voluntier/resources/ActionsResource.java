package voluntier.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import voluntier.util.userdata.Account;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.Roles;
import voluntier.util.userdata.State;

public class ActionsResource {
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory rolesFactory = datastore.newKeyFactory().setKind("Role");

	private enum Action {
		REMOVE, MODIFY_ATRIB, MODIFY_STATE, MODIFY_ROLE, LOOK_UP
	}

	public static boolean hasRemovePermission(Entity user, Entity target, Transaction txn) {
		if (isRemovedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.REMOVE, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.REMOVE, Roles.valueOf(target.getString(DB_User.ROLE)), txn);

		return target_level < user_level || target.getString(DB_User.EMAIL).equals(user.getString(DB_User.EMAIL));
	}

	public static boolean hasLookUpPermission(Entity user, Entity target, Transaction txn) {
		if (isRemovedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.LOOK_UP, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.LOOK_UP, Roles.valueOf(target.getString(DB_User.ROLE)), txn);

		return target_level < user_level || target.getString(DB_User.EMAIL).equals(user.getString(DB_User.EMAIL));
	}
	
	public static boolean hasSamePermission (Entity user, Entity target, Transaction txn) {
		if (isRemovedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.LOOK_UP, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.LOOK_UP, Roles.valueOf(target.getString(DB_User.ROLE)), txn);

		return target_level == user_level || target.getString(DB_User.EMAIL).equals(user.getString(DB_User.EMAIL));
	}

	public static boolean hasAtribPermission(Entity user, Entity target, Transaction txn) {
		if (isRemovedOrBannedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.MODIFY_ATRIB, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.MODIFY_ATRIB, Roles.valueOf(target.getString(DB_User.ROLE)), txn);

		return target_level < user_level || target.getString(DB_User.EMAIL).equals(user.getString(DB_User.EMAIL));
	}

	public static boolean hasRolePermission(Entity user, Entity target, Transaction txn, String to) {
		if (isRemovedOrBannedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(target.getString(DB_User.ROLE)), txn);
		int desired_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(to), txn);

		return target_level + 1 < user_level && desired_level < user_level && desired_level != target_level;
	}

	public static boolean hasStatePermission(Entity user, Entity target, Transaction txn) {
		if (isRemovedUser(target) || isRemovedOrBannedUser(user))
			return false;

		int user_level = getLevel(Action.MODIFY_STATE, Roles.valueOf(user.getString(DB_User.ROLE)), txn);
		int target_level = getLevel(Action.MODIFY_STATE, Roles.valueOf(target.getString(DB_User.ROLE)), txn);

		return target_level < user_level;
	}
	
	public static boolean hasRoutePermission(Entity user) {
		if (isRemovedOrBannedUser(user))
			return false;

		Roles role = Roles.valueOf(user.getString(DB_User.ROLE));
		return role == Roles.GBO || role == Roles.GA || role == Roles.SU;
	}
	
	public static boolean hasEventPermission(Entity user) {
		if (isRemovedOrBannedUser(user))
			return false;

		Roles role = Roles.valueOf(user.getString(DB_User.ROLE));
		return role == Roles.GBO || role == Roles.GA || role == Roles.SU;
	}
	
	public static boolean hasCausePermission(Entity user) {
		if (isRemovedOrBannedUser(user))
			return false;

		Roles role = Roles.valueOf(user.getString(DB_User.ROLE));
		return role == Roles.GA || role == Roles.SU;
	}

	public static boolean isRemovedUser(Entity user) {
		return user.getString(DB_User.ACCOUNT).equals(Account.REMOVED.toString());
	}

	public static boolean isBannedUser(Entity user) {
		return user.getString(DB_User.STATE).equals(State.BANNED.toString());
	}

	public static boolean isRemovedOrBannedUser(Entity user) {
		return user.getString(DB_User.ACCOUNT).equals(Account.REMOVED.toString())
				|| user.getString(DB_User.STATE).equals(State.BANNED.toString());
	}

	private static int getLevel(Action action, Roles role, Transaction txn) {
		Key user_key = rolesFactory.newKey(role.toString());
		Entity user_role = txn.get(user_key);

		return (int) user_role.getLong(action.toString());
	}
	
	public static boolean isSU (Entity user) {
		return user.getString(DB_User.ROLE).equals(Roles.SU.toString());
	}

}
