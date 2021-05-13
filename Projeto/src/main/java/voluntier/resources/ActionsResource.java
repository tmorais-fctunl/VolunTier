package voluntier.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import voluntier.util.Roles;

public class ActionsResource {
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory rolesFactory = datastore.newKeyFactory().setKind("Role");

	private enum Action {
		REMOVE,
		MODIFY_ATRIB,
		MODIFY_STATE,
		MODIFY_ROLE,
		LOOK_UP
	}

	public static boolean hasRemovePermission(Entity user, Entity target, Transaction txn) {
		int user_level = getLevel(Action.REMOVE, Roles.valueOf(user.getString("user_role")), txn);
		int target_level = getLevel(Action.REMOVE, Roles.valueOf(target.getString("user_role")), txn);
		
		return target_level < user_level || target.getString("user_id").equals(user.getString("user_id"));
	}
	
	public static boolean hasLookUpPermission(Entity user, Entity target, Transaction txn) {
		int user_level = getLevel(Action.LOOK_UP, Roles.valueOf(user.getString("user_role")), txn);
		int target_level = getLevel(Action.LOOK_UP, Roles.valueOf(target.getString("user_role")), txn);
		
		return target_level < user_level;
	}
	
	public static boolean hasAtribPermission(Entity user, Entity target, Transaction txn) {
		int user_level = getLevel(Action.MODIFY_ATRIB, Roles.valueOf(user.getString("user_role")), txn);
		int target_level = getLevel(Action.MODIFY_ATRIB, Roles.valueOf(target.getString("user_role")), txn);
		
		return target_level < user_level || target.getString("user_id").equals(user.getString("user_id"));
	}
	
	public static boolean hasRolePermission(Entity user, Entity target, Transaction txn, String to) {
		int user_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(user.getString("user_role")), txn);
		int target_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(target.getString("user_role")), txn);
		int desired_level = getLevel(Action.MODIFY_ROLE, Roles.valueOf(to), txn);
		
		return target_level + 1 < user_level && desired_level < user_level && desired_level != target_level;
	}
	
	public static boolean hasStatePermission(Entity user, Entity target, Transaction txn) {
		int user_level = getLevel(Action.MODIFY_STATE, Roles.valueOf(user.getString("user_role")), txn);
		int target_level = getLevel(Action.MODIFY_STATE, Roles.valueOf(target.getString("user_role")), txn);
		
		return target_level < user_level;
	}
	
	private static int getLevel(Action action, Roles role, Transaction txn) {		
		Key user_key = rolesFactory.newKey(role.toString());
		Entity user_role = txn.get(user_key);
		
		return (int) user_role.getLong(action.toString());
	}
}
