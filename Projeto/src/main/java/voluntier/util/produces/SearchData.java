package voluntier.util.produces;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Entity;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.userdata.DB_User;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

public class SearchData {
	public String[] cursor;
	public String results;

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

	public SearchData(Triplet<List<Entity>, Cursor[], QueryResultBatch.MoreResultsType> data) {
		List<Entity> entities = data.getValue0();
		users = new LinkedList<>();
		entities.forEach(entity -> {
			users.add(new UserSearchData(entity.getString(DB_User.USERNAME), entity.getString(DB_User.FULL_NAME),
					entity.getString(DB_User.EMAIL), entity.getString(DB_User.PROFILE)));
		});

		results = data.getValue2().toString();

		if (data.getValue2() != MoreResultsType.NO_MORE_RESULTS) {
			cursor = new String[2];
			cursor[0] = data.getValue1()[0].toUrlSafe();
			cursor[1] = data.getValue1()[1].toUrlSafe();
		}
	}
}
