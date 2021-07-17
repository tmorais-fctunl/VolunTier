package voluntier.util.routedata;

import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;

import voluntier.exceptions.RouteAlreadyExistsException;
import voluntier.util.DB_Util;

public class DB_Rating {
	
	public static final String ID = "rating_id";
	public static final String SUM = "rating_sum";
	public static final String USERS = "rated_users";

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory ratingFactory = datastore.newKeyFactory().setKind("Rating");
	
	private static DB_Util util = new DB_Util((e, builder) -> {
		builder = Entity.newBuilder(e.getKey())
				.set(ID, e.getString(ID))
				.set(USERS, e.getList(USERS))
				.set(SUM, e.getLong(SUM));
	});
	
	private static Key generateKey() {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Rating" + rand.nextInt();
			idKey = ratingFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}
	
	public static Pair<Entity, String> createNew() {

		Key key = generateKey();
		String id = key.getName();

		ListValue.Builder users = ListValue.newBuilder();
		
		Entity rating = Entity.newBuilder(key)
				.set(ID, id)
				.set(SUM, 0)
				.set(USERS, users.build())
				.build();
		
		return new Pair<>(rating, id);
	}

}
