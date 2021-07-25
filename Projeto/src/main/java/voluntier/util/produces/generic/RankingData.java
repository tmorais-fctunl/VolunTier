package voluntier.util.produces.generic;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Triplet;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Entity;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.data.event.RankingType;
import voluntier.util.data.user.DB_User;

public class RankingData {

	public String cursor;
	public String results;

	public static class UserSearchData {
		public String username;
		public String full_name;
		public String email;
		public int score;
		public Integer rank;
		public String pic_64;

		public UserSearchData(Entity user, int score, Integer rank) {
			this.username = user.getString(DB_User.USERNAME);
			this.full_name = user.getString(DB_User.FULL_NAME);
			this.email = user.getString(DB_User.EMAIL);
			String pic = user.getString(DB_User.PROFILE_PICTURE_MINIATURE);
			this.pic_64 = pic.equals("") ? null : pic;
			this.score = score;
			this.rank = rank;
		}
	}

	List<UserSearchData> users;

	UserSearchData current_user;

	public RankingData(Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> data, String rank_type,
			UserSearchData current_user) {
		List<Entity> entities = data.getValue0();
		users = new LinkedList<>();
		entities.forEach(entity -> {
			users.add(new UserSearchData(entity,
					rank_type.equals(RankingType.TOTAL_CURRENCY.toString())
							? (int) entity.getDouble(DB_User.TOTAL_CURRENCY)
							: (int) entity.getLong(DB_User.N_EVENTS_PARTICIPATED),
					null));
		});

		results = data.getValue2().toString();

		if (data.getValue2() != MoreResultsType.NO_MORE_RESULTS)
			cursor = data.getValue1().toUrlSafe();

		this.current_user = current_user;
	}
}
