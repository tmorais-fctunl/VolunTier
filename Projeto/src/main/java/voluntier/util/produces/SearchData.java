package voluntier.util.produces;

import com.google.cloud.datastore.Entity;

import voluntier.util.userdata.DB_User;

import java.util.LinkedList;
import java.util.List;

public class SearchData {
	public class UserSearchData {
		public String username;
		public String full_name;
		public String profile;
		public String email;

		public UserSearchData(String username, String full_name, String email, String profile) {
			this.username = username;
			this.full_name = full_name;
			this.profile = profile;
			this.email = email;
		}
	}

	List<UserSearchData> users;

	public SearchData(List<Entity> queriedEntities) {
		users = new LinkedList<>();
		queriedEntities.forEach(entity -> {
			users.add(new UserSearchData(entity.getString(DB_User.USERNAME), entity.getString(DB_User.FULL_NAME),
					entity.getString(DB_User.EMAIL), entity.getString(DB_User.PROFILE)));
		});
	}
}
