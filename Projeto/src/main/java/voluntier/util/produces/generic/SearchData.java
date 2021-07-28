package voluntier.util.produces.generic;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Entity;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.resources.ActionsResource;
import voluntier.util.data.user.DB_User;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Triplet;

public class SearchData {
	public String[] cursor;
	public String results;

	public class UserSearchData {
		public String username;
		public String full_name;
		public String profile;
		public String email;
		public String pic_64;

		public String role;
		public String account;
		public String state;

		public UserSearchData(String username, String full_name, String email, String profile, String encodedPicture,
				String role, String account, String state) {
			this.username = username;
			this.full_name = full_name;
			this.profile = profile;
			this.email = email;
			this.pic_64 = encodedPicture;

			this.role = role;
			this.account = account;
			this.state = state;
		}
	}

	List<UserSearchData> users;

	public SearchData(Triplet<List<Entity>, Cursor[], QueryResultBatch.MoreResultsType> data, Entity user) {
		List<Entity> entities = data.getValue0();
		users = new LinkedList<>();
		entities.forEach(entity -> {
			String pic = entity.getString(DB_User.PROFILE_PICTURE_MINIATURE);
			if (ActionsResource.hasGBOPermission(user))
				users.add(new UserSearchData(entity.getString(DB_User.USERNAME), entity.getString(DB_User.FULL_NAME),
						entity.getString(DB_User.EMAIL), entity.getString(DB_User.PROFILE), pic.equals("") ? null : pic,
								entity.getString(DB_User.ROLE), entity.getString(DB_User.ACCOUNT), entity.getString(DB_User.STATE)) );
			else
				users.add(new UserSearchData(entity.getString(DB_User.USERNAME), entity.getString(DB_User.FULL_NAME),
						entity.getString(DB_User.EMAIL), entity.getString(DB_User.PROFILE), pic.equals("") ? null : pic,
								null, null, null) );
		});

		results = data.getValue2().toString();

		if (data.getValue2() != MoreResultsType.NO_MORE_RESULTS) {
			cursor = new String[2];
			cursor[0] = data.getValue1()[0].toUrlSafe();
			cursor[1] = data.getValue1()[1].toUrlSafe();
		}
	}
}
