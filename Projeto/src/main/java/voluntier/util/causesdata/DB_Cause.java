package voluntier.util.causesdata;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.InexistentCauseException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.NotEnoughCurrencyException;
import voluntier.util.DB_Util;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.causes.CreateCauseData;
import voluntier.util.produces.AllCausesDataReturn;
import voluntier.util.produces.CauseDataReturn;
import voluntier.util.produces.DonatorDataReturn;
import voluntier.util.produces.DonatorsDataReturn;
import voluntier.util.produces.DownloadPictureReturn;
import voluntier.util.produces.DownloadSignedURLReturn;
import voluntier.util.routedata.PictureData;
import voluntier.util.userdata.Account;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.State;

public class DB_Cause {
	public static final String ID = "cause_id";
	public static final String NAME = "cause_name";
	public static final String GOAL = "cause_goal";
	public static final String IMAGES = "cause_images";
	public static final String WEBSITE = "cause_website";
	public static final String DESCRIPTION = "cause_description";
	public static final String DONATORS = "cause_givers";
	public static final String RAISED = "cause_raised";
	public static final String WITH = "cause_company_name";
	public static final String CREATOR = "cause_creator";
	public static final String CREATION_DATE = "cause_creation_date";
	public static final String LAST_UPDATE = "cause_last_update";
	public static final String STATUS = "cause_status";
	private static final int MAX_DONATORS_RETURN = 5;
	
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory causesFactory = datastore.newKeyFactory().setKind("Cause");
	
	private static DB_Util util = new DB_Util(DB_Cause::defaultBuilder);
	
	private static void defaultBuilder(Entity cause) {
		util.builder = Entity.newBuilder(cause.getKey())
				.set(ID, cause.getString(ID))
				.set(NAME, cause.getString(NAME))
				.set(GOAL, cause.getDouble(GOAL))
				.set(IMAGES, cause.getList(IMAGES))
				.set(WEBSITE, cause.getString(WEBSITE))
				.set(DESCRIPTION, cause.getString(DESCRIPTION))
				.set(DONATORS, cause.getList(DONATORS))
				.set(RAISED, cause.getDouble(RAISED))
				.set(WITH, cause.getString(WITH))
				.set(CREATOR, cause.getString(CREATOR))
				.set(CREATION_DATE, cause.getString(CREATION_DATE))
				.set(LAST_UPDATE, cause.getLong(LAST_UPDATE))
				.set(STATUS, cause.getString(STATUS));
	}

	private static Key generateChatID() {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Cause" + rand.nextInt();
			idKey = causesFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}

	public static Pair<Entity, String> createNew(CreateCauseData create_data) {
		Key idKey = generateChatID();
		String id = idKey.getName();
		
		ListValue.Builder images = DB_Util.emptyList();
		
		for(int i = 0; i < create_data.num_images; ++i) {
			String img_id = generateNewPictureID(id, i);
			images.addValue(JsonUtil.json.toJson(new PictureData(img_id, null, null)));
		}

		Entity cause = Entity.newBuilder(idKey)
				.set(ID, id)
				.set(NAME, create_data.cause_name)
				.set(GOAL, create_data.cause_goal)
				.set(IMAGES, images.build())
				.set(WEBSITE, create_data.cause_website)
				.set(DESCRIPTION, create_data.description)
				.set(DONATORS, DB_Util.emptyList().build())
				.set(RAISED, 0.0)
				.set(WITH, create_data.company_name)
				.set(CREATOR, create_data.email)
				.set(CREATION_DATE, Timestamp.now())
				.set(LAST_UPDATE, System.currentTimeMillis())
				.set(STATUS, Account.ACTIVE.toString())
				.build();

		return new Pair<>(cause, id);
	}
	
	public static List<DownloadPictureReturn> getImagesDownloadURLs(String cause_id) throws InexistentCauseException {
		Entity cause = getCause(cause_id);
		return getImagesDownloadURLs(cause);
	}
	
	public static List<DownloadPictureReturn> getImagesDownloadURLs(Entity cause) {
		List<PictureData> images = DB_Util.getJsonList(cause, IMAGES, PictureData.class);
		List<DownloadPictureReturn> download_urls = new LinkedList<>();

		images.forEach(image -> {
			Pair<URL, Long> url = GoogleStorageUtil.signURLForDownload(image.picture_id);
			DownloadSignedURLReturn dwld_url = new DownloadSignedURLReturn(url.getValue0(), url.getValue1());
			download_urls.add(new DownloadPictureReturn(dwld_url, image.picture_id, image.timestamp, null));
		});

		return download_urls;
	}
	
	private static String generateNewPictureID(String id, int count) {
		return id + "-" + (count + 1);
	}
	
	public static List<Entity> donate(String cause_id, String user_email, float amount) 
			throws InexistentCauseException, InexistentUserException, NotEnoughCurrencyException {
		Entity cause = getCause(cause_id);
		Entity user = DB_User.getUser(user_email);
		
		List<Entity> ents = new LinkedList<>();
		ents.add(DB_User.donate(user, amount, cause_id, cause.getString(NAME)));
		ents.add(util.addJsonToList(cause, DONATORS, new DonatorsData(user.getString(DB_User.EMAIL), amount, Timestamp.now().toString())));
		
		return ents;
	}
	
	public static DonatorsDataReturn getDonators(String cause_id, int cursor) throws InexistentCauseException, InexistentUserException {
		Entity cause = getCause(cause_id);
		
		Triplet<List<DonatorDataReturn>, Integer, MoreResultsType> res = getDonators(cause, cursor);
		List<DonatorDataReturn> donators = res.getValue0();
		int new_cursor = res.getValue1();
		MoreResultsType more_results = res.getValue2();
		
		return new DonatorsDataReturn(donators, new_cursor, more_results);
	}
	
	private static Triplet<List<DonatorDataReturn>, Integer, MoreResultsType> getDonators(Entity cause, int cursor) throws InexistentUserException {
		List<DonatorsData> donators_data = DB_Util.getJsonList(cause, DONATORS, DonatorsData.class);
		List<DonatorDataReturn> donators_data_return = new LinkedList<>();
		
		int i = 0;
		int counter = 0;
		for (DonatorsData donator : donators_data) {
			if (counter + 1 > MAX_DONATORS_RETURN)
				break;
			if (++i <= cursor)
				continue;

			++counter;

			Entity user = DB_User.getUser(donator.email);
			String encodedPicture = user.getString(DB_User.PROFILE_PICTURE_MINIATURE);
			String username = user.getString(DB_User.USERNAME);

			donators_data_return
					.add(new DonatorDataReturn(encodedPicture, username, donator.email, donator.donation, donator.timestamp));
		}

		int new_cursor = i;
		boolean more_results = new_cursor < donators_data.size();
		return new Triplet<>(donators_data_return, more_results ? new_cursor : null,
				more_results ? MoreResultsType.MORE_RESULTS_AFTER_LIMIT : MoreResultsType.NO_MORE_RESULTS);
	}

	public static AllCausesDataReturn getCauses(){
		List<CauseDataReturn> causes = new LinkedList<>();
		List<Entity> queried_causes = queryAllCauses();
		
		queried_causes.forEach(c -> causes.add(new CauseDataReturn(c)));
		
		return new AllCausesDataReturn(causes);
	}
	
	public static boolean checkUpdates(String cause_id, long check_time_millis) throws InexistentCauseException{
		getCause(cause_id);
		return queryUpdates(cause_id, check_time_millis);
	}

	private static List<Entity> queryAllCauses() {
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind("Cause")
				.setFilter(PropertyFilter.eq(STATUS, Account.ACTIVE.toString()));

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> causes = new LinkedList<>();
		res.forEachRemaining(e -> {
			causes.add(e);
		});

		return causes;
	}
	
	private static boolean queryUpdates(String cause_id, Long check_time_millis) {
		Builder<Key> b = Query.newKeyQueryBuilder().setKind("Cause")
				.setFilter(CompositeFilter.and(PropertyFilter.ge(LAST_UPDATE, check_time_millis),
						PropertyFilter.eq(STATUS, State.ENABLED.toString()),
						PropertyFilter.eq(ID, cause_id)));

		Query<Key> query = b.build();

		QueryResults<Key> res = datastore.run(query);

		List<Key> causes = new LinkedList<>();
		res.forEachRemaining(e -> {
			causes.add(e);
		});

		return causes.size() > 0;
	}
	
	public static Entity getCause(String cause_id) throws InexistentCauseException {
		Key causeKey = causesFactory.newKey(cause_id);
		Entity cause = datastore.get(causeKey);

		if (cause == null)
			throw new InexistentCauseException("11: No cause with id: " + cause_id);

		return cause;
	}
}
