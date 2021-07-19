package voluntier.util.chatdata;

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

import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.util.DB_Util;
import voluntier.util.eventdata.MessageData;
import voluntier.util.eventdata.MessageDataReturn;
import voluntier.util.rating.DB_Rating;

public class DB_MessageLog {
	public static final String MESSAGES = "messages";
	public static final String CHAT_ID = "chat_id";
	public static final String ID = "id";
	public static final String START_INDEX = "start_index";

	public static final int MAX_LOG_SIZE = 1000;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory logFactory = datastore.newKeyFactory().setKind("MessageLog");
	
	private static DB_Util util = new DB_Util(DB_MessageLog::defaultBuilder);
	
	private static void defaultBuilder(Entity ml) {
		util.builder = Entity.newBuilder(ml.getKey())
				.set(ID, ml.getString(ID))
				.set(CHAT_ID, ml.getString(CHAT_ID))
				.set(START_INDEX, ml.getLong(START_INDEX))
				.set(MESSAGES, ml.getList(MESSAGES));
	}

	private static Key generateLogID() {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Log" + rand.nextInt();
			idKey = logFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}

	public static Pair<Entity, String> createNew(String chat_id, int start_index) {
		Key idKey = generateLogID();

		ListValue.Builder messages = ListValue.newBuilder();

		return new Pair<>(Entity.newBuilder(idKey)
				.set(ID, idKey.getName())
				.set(CHAT_ID, chat_id)
				.set(MESSAGES, messages.build())
				.set(START_INDEX, start_index)
				.build(), idKey.getName());
	}

	public static MessageData getMessage(String log_id, int message_id)
			throws InexistentLogIdException, InexistentMessageIdException {
		Entity log = getLog(log_id);		

		MessageData message = DB_Util.findInJsonList(log, MESSAGES, (m -> message_id == m.comment_id), MessageData.class);
		if(message == null)
			throw new InexistentMessageIdException();
		return message;
	}

	public static Triplet<List<Entity>, String, Integer> createLogAndAddMessage(String chat_id, int start_index, String email, String username, 
			String message) {
		Pair<Entity, String> newLog = createNew(chat_id, start_index);
		String message_log_id = newLog.getValue1();
		Pair<List<Entity>, Integer> updated_log;

		updated_log = addMessage(newLog.getValue0(), email, username, message);
		Integer message_id = updated_log.getValue1();

		return new Triplet<>(updated_log.getValue0(), message_log_id, message_id);
	}

	private static Pair<List<Entity>, Integer> addMessage(Entity log, String email, String username, String message) {
		List<MessageData> messages = DB_Util.getJsonList(log, MESSAGES, MessageData.class);
		Pair<Entity, String> rating = DB_Rating.createNew();

		int message_id = (messages.size() > 0 ? messages.get(messages.size() - 1).comment_id + 1 : (int) log.getLong(START_INDEX));
		MessageData message_data = new MessageData(email, username, message, Timestamp.now().toString(), message_id, rating.getValue1());

		log = util.addJsonToList(log, MESSAGES, message_data);
		
		List<Entity> ents = new LinkedList<>();
		ents.add(log);
		ents.add(rating.getValue0());

		return new Pair<>(ents, message_id);
	}

	public static Pair<List<Entity>, Integer> addMessage(String log_id, String email, String username, String message)
			throws InexistentLogIdException, MaximumSizeReachedException {
		Entity log = getLog(log_id);
		
		if (Entity.calculateSerializedSize(log) + message.length() > MAX_LOG_SIZE)
			throw new MaximumSizeReachedException();

		return addMessage(log, email, username, message);
	}

	public static Entity deleteMessage(String log_id, int message_id)
			throws InexistentLogIdException, InexistentMessageIdException {
		Entity log = getLog(log_id);
		
		MessageData data = DB_Util.findInJsonList(log, MESSAGES, (m -> message_id == m.comment_id), MessageData.class);
		if(data != null) {
			try {
				return util.removeJsonFromList(log, MESSAGES, data);
			} catch (InexistentElementException e) {}
		}
		
		throw new InexistentMessageIdException("There is no message with id: " + message_id + "."); 
	}

	public static Entity editMessage(String log_id, int message_id, String new_message)
			throws InexistentLogIdException, InexistentMessageIdException {
		Entity log = getLog(log_id);
		
		List<MessageData> messages = DB_Util.getJsonList(log, MESSAGES, MessageData.class);
		messages.forEach(message -> {
			if(message.comment_id == message_id)
				message.comment = new_message;
		});
		
		return util.setJsonListProperty(log, MESSAGES, messages);
	}

	public static Entity giveOrRemoveLikeInMessage(String log_id, int message_id, String req_email)
			throws InexistentMessageIdException, InexistentLogIdException, InexistentRatingException {
		Entity log = getLog(log_id);
		
		MessageData message = DB_Util.findInJsonList(log, MESSAGES, (m -> message_id == m.comment_id), MessageData.class);
		
		if(message == null)
			throw new InexistentMessageIdException("There is no message with id: " + message_id + ".");
		
		return DB_Rating.giveOrRemoveLike(message.rating_id, req_email);
	}

	public static List<MessageDataReturn> getMessages(String log_id, boolean latest_first) throws InexistentLogIdException {
		Entity log = getLog(log_id);
		
		List<MessageData> messages = DB_Util.getJsonList(log, MESSAGES, MessageData.class);

		List<MessageDataReturn> out = new LinkedList<>();
		messages.forEach(message -> {
			MessageDataReturn return_data = new MessageDataReturn(message);
			if (latest_first)
				out.add(0, return_data);
			else
				out.add(return_data);
		});

		return out;
	}
	
	private static Entity getLog(String log_id) throws InexistentLogIdException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();
		
		return log;
	}

}
