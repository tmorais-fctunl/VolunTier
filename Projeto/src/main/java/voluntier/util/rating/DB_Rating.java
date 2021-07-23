package voluntier.util.rating;

import java.util.Random;

import org.javatuples.Pair;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;

import java.util.List;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.exceptions.InexistentUserException;
import voluntier.util.DB_Util;

public class DB_Rating {
	
	public static final String ID = "rating_id";
	public static final String SUM = "rating_sum";
	public static final String USERS = "rated_users";

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory ratingFactory = datastore.newKeyFactory().setKind("Rating");
	
	private static DB_Util util = new DB_Util(DB_Rating::defaultBuilder);
	
	private static void defaultBuilder (Entity e) {
		util.builder = Entity.newBuilder(e.getKey())
				.set(ID, e.getString(ID))
				.set(USERS, e.getList(USERS))
				.set(SUM, e.getDouble(SUM));
	}
	
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
				.set(SUM, 0.0)
				.set(USERS, users.build())
				.build();
		
		return new Pair<>(rating, id);
	}
	
	public static Entity getRating(String rating_id) throws InexistentRatingException {
		Key key = ratingFactory.newKey(rating_id);
		Entity rating = datastore.get(key);

		if (rating == null)
			throw new InexistentRatingException("11: No rating with id: " + rating_id);

		return rating;
	}
	
	public static Entity giveRating(String rating_id, double rating_number, String user_email) throws InexistentRatingException {
		Entity rating = getRating(rating_id);

		double current_sum = rating.getDouble(SUM);
		
		try {
			UserRatingData old_rating = DB_Util.findInJsonList(rating, USERS, 
					(u -> u.user_email.equals(user_email)), UserRatingData.class);

			if(old_rating != null) {
				rating = util.removeJsonFromList(rating, USERS, old_rating);
				current_sum -= old_rating.rating;
			}

			UserRatingData data = new UserRatingData(user_email, rating_number);

			rating = util.addUniqueJsonToList(rating, USERS, data);
			rating = util.updateProperty(rating, SUM, DoubleValue.of(current_sum + rating_number));

		} catch (AlreadyExistsException | InexistentElementException e) {}
		
		return rating;
	}
	
	public static Entity removeRating(String rating_id, String user_email) throws InexistentRatingException, InexistentUserException {
		Entity rating = getRating(rating_id);
		double current_sum = rating.getDouble(SUM);
		try {
			UserRatingData old_rating = DB_Util.findInJsonList(rating, USERS, 
					(u -> u.user_email.equals(user_email)), UserRatingData.class);
			if(old_rating != null) {
				rating = util.removeJsonFromList(rating, USERS, old_rating);
			} else
				throw new InexistentUserException("User has not rated yet: " + rating_id);
			
			rating = util.updateProperty(rating, SUM, DoubleValue.of(current_sum - old_rating.rating));
			
		} catch (InexistentElementException e) {}
		
		return rating;
	}
		
	public static Entity giveOrRemoveLike(String rating_id, String user_email) throws InexistentRatingException {
		Entity rating = getRating(rating_id);
		try {
			if(DB_Util.existsInJsonList(rating, USERS, (u -> u.user_email.equals(user_email)), UserRatingData.class))
				return removeRating(rating_id, user_email);
			else
				return giveRating(rating_id, 1, user_email);
		} catch (InexistentUserException e) {} // this exception comes from removeRating but we already check its existence
		
		return rating;
	}
	
	public static boolean hasRated(String rating_id, String user_email) throws InexistentRatingException {
		Entity rating = getRating(rating_id);
		return DB_Util.existsInJsonList(rating, USERS, (u -> u.user_email.equals(user_email)), UserRatingData.class);
	}
	
	public static UserRatingData getUserRating(String rating_id, String user_email) throws InexistentRatingException {
		Entity rating = getRating(rating_id);
		return DB_Util.findInJsonList(rating, USERS, (u -> u.user_email.equals(user_email)), UserRatingData.class);
	}
	
	public static double getSumRating(String rating_id) throws InexistentRatingException {
		Entity rating = getRating(rating_id);
		double sum = rating.getDouble(SUM);
		
		return sum;
	}
	
	public static double getAverageRating(String rating_id) throws InexistentRatingException {
		Entity rating = getRating(rating_id);
		double sum = rating.getDouble(SUM);
		List<Value<?>> users = rating.getList(USERS);
		
		if(users.size() == 0)
			return 0.0;
		
		return sum / (double) users.size();
	}

}
