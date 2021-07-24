package voluntier.util.chatdata;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.DB_Util;
import voluntier.util.JsonUtil;
import voluntier.util.eventdata.MessageData;
import voluntier.util.eventdata.MessageDataReturn;
import voluntier.util.produces.ChatReturn;

public class DB_Chat {
	public static final String MLs = "chat_message_logs";
	public static final String MODS = "chat_moderators";
	public static final String ADMIN = "chat_admin";
	public static final String ID = "chat_id";

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory chatFactory = datastore.newKeyFactory().setKind("Chat");
	
	private static DB_Util util = new DB_Util(DB_Chat::defaultBuilder);
	
	private static void defaultBuilder(Entity chat) {
		util.builder = Entity.newBuilder(chat.getKey())
				.set(ID, chat.getString(ID))
				.set(MODS, chat.getList(MODS))
				.set(MLs, chat.getList(MLs))
				.set(ADMIN, chat.getString(ADMIN));
	}

	private static Key generateChatID() {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Chat" + rand.nextInt();
			idKey = chatFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}

	public static Pair<List<Entity>, String> createNew(String admin_email) {
		Key idKey = generateChatID();
		ListValue.Builder moderators = ListValue.newBuilder();
		ListValue.Builder message_logs = ListValue.newBuilder();
		Pair<Entity, String> message_log = DB_MessageLog.createNew(idKey.getName(), 0);
		message_logs.addValue(JsonUtil.json.toJson(new MessageLog(message_log.getValue1(), 0)));

		List<Entity> ents = new LinkedList<>();
		ents.add(Entity.newBuilder(idKey)
				.set(ID, idKey.getName())
				.set(MODS, moderators.build())
				.set(MLs, message_logs.build())
				.set(ADMIN, admin_email)
				.build());
		
		ents.add(message_log.getValue0());

		return new Pair<>(ents, idKey.getName());
	}

	private static Entity addMessageLog(Key key, Entity chat, MessageLog data) {
		return util.addJsonToList(chat, MLs, data);
	}

	private static Entity addModerator(Key key, Entity chat, String email) throws AlreadyExistsException {
		return util.addUniqueStringToList(chat, MODS, email);
	}

	private static Entity removeModerator(Key key, Entity chat, String email) throws InexistentModeratorException {
		try {
			return util.removeStringFromList(chat, MODS, email);
		} catch (InexistentElementException e) {
			throw new InexistentModeratorException("No moderator with the given email");
		}
	}

	private static MessageLog getLastMessageLog(Entity chat) {
		List<Value<?>> message_logs = chat.getList(MLs);
		Value<?> last_log = message_logs.get(message_logs.size() - 1);
		MessageLog last_log_data = JsonUtil.json.fromJson((String) last_log.get(), MessageLog.class);
		
		return last_log_data;
	}

	private static MessageLog getMessageLogWithMessageId(Entity chat, Integer message_id) {
		return DB_Util.findLastInJsonList(chat, MLs, (ml -> ml.start_index <= message_id) , MessageLog.class);
	}

	private static List<MessageLog> getMessageLogs(Entity chat) {
		return DB_Util.getJsonList(chat, MLs, MessageLog.class);
	}

	private static List<String> getModeratorList(Entity chat) {
		return DB_Util.getStringList(chat, MODS);
	}

	public static Pair<List<Entity>, Integer> postMessage(String chat_id, String email, String username, String message)
			throws InexistentChatIdException, InexistentLogIdException, SomethingWrongException {
		Entity chat = getChat(chat_id);

		List<Entity> ents = new LinkedList<>();

		// get the last MessageLog on the list.
		MessageLog last_log_data = getLastMessageLog(chat);

		String last_log_id = last_log_data.id;
		int message_id = -1;

		try {
			Pair<List<Entity>, Integer> message_log = DB_MessageLog.addMessage(last_log_id, email, username, message);
			List<Entity> logs_and_ratings = message_log.getValue0();
			logs_and_ratings.forEach(e -> ents.add(e));
			
			message_id = message_log.getValue1();

		} catch (MaximumSizeReachedException e) {
			// need to create a new MessageLog and add it to current list with the new message
			//get the last index from the last log, +1 will be the starting index of the new log
			List<MessageDataReturn> messages = DB_MessageLog.getMessages(last_log_id, false, email);
			int new_start_index = messages.get(messages.size() - 1).comment_id + 1;

			Triplet<List<Entity>, String, Integer> new_message_log = DB_MessageLog.createLogAndAddMessage(chat_id,
					new_start_index, email, username, message);

			String message_log_id = new_message_log.getValue1();
			message_id = new_message_log.getValue2();

			Entity newChatMessageLogs = addMessageLog(chat.getKey(), chat, new MessageLog(message_log_id, new_start_index));
			ents.add(newChatMessageLogs);
			
			List<Entity> logs_ratings = new_message_log.getValue0();
			logs_ratings.forEach(r -> ents.add(r));
		}

		return new Pair<>(ents, message_id);
	}

	public static Entity deleteMessage(String chat_id, int message_id, String email) throws InexistentChatIdException,
			InexistentLogIdException, InexistentMessageIdException, ImpossibleActionException {
		Entity chat = getChat(chat_id);

		if(message_id < 0)
			throw new InexistentMessageIdException("inexistent message");

		MessageLog log_data = getMessageLogWithMessageId(chat, message_id);

		MessageData message = DB_MessageLog.getMessage(log_data.id, message_id);

		List<String> mods = getModeratorList(chat);
		if (!message.email.equals(email) && !mods.contains(email) && !email.equals(chat.getString(ADMIN)))
			throw new ImpossibleActionException("no permission");

		Entity newMessageLog = DB_MessageLog.deleteMessage(log_data.id, message_id);

		return newMessageLog;
	}

	public static Entity editMessage(String chat_id, int message_id, String email, String new_message)
			throws InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException,
			ImpossibleActionException {
		Entity chat = getChat(chat_id);

		MessageLog log_data = getMessageLogWithMessageId(chat, message_id);

		MessageData message = DB_MessageLog.getMessage(log_data.id, message_id);
		if (!message.email.equals(email))
			throw new ImpossibleActionException();

		Entity newMessageLog = DB_MessageLog.editMessage(log_data.id, message_id, new_message);

		return newMessageLog;
	}

	public static Entity giveOrRemoveLikeInMessage(String chat_id, int message_id, String req_email)
			throws InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException, InexistentRatingException {
		Entity chat = getChat(chat_id);

		MessageLog log_data = getMessageLogWithMessageId(chat, message_id);

		Entity newRating = DB_MessageLog.giveOrRemoveLikeInMessage(log_data.id, message_id, req_email);

		return newRating;
	}
	
	public static ChatReturn getChat(String chat_id,
			int cursor, boolean latest_first, String user_email) throws InexistentChatIdException, InvalidCursorException, InexistentLogIdException {

		Entity chat = getChat(chat_id);

		List<MessageLog> logs = getMessageLogs(chat);

		if (cursor >= logs.size() || cursor < 0)
			throw new InvalidCursorException();
		
		int index = latest_first ? logs.size() - 1 - cursor : cursor;

		MessageLog log = logs.get(index);
		boolean more_results = cursor < logs.size() - 1;
		
		List<MessageDataReturn> messages = DB_MessageLog.getMessages(log.id, latest_first, user_email);
		
		int new_cursor = cursor + 1;
		
		if(latest_first && cursor == 0 && logs.size() > 1) {
			messages.addAll(DB_MessageLog.getMessages(logs.get(index - 1).id, latest_first, user_email));
			more_results = cursor < logs.size() - 2;
			new_cursor = cursor + 2;
		}
		
		if(more_results && messages.size() == 0)
			return getChat(chat_id, new_cursor, latest_first, user_email);

		return new ChatReturn(messages, more_results ? new_cursor : null,
				more_results ? MoreResultsType.MORE_RESULTS_AFTER_LIMIT : MoreResultsType.NO_MORE_RESULTS);
	}

	public static Entity makeModerator(String chat_id, String target_email, String req_email)
			throws InexistentChatIdException, ImpossibleActionException {

		Entity chat = getChat(chat_id);

		if (!req_email.equals(chat.getString(ADMIN)))
			throw new ImpossibleActionException();

		try {
			return addModerator(chat.getKey(), chat, target_email);
		} catch (AlreadyExistsException e) {
			throw new ImpossibleActionException();
		}
	}

	public static Entity removeModerator(String chat_id, String target_email, String req_email)
			throws InexistentChatIdException, ImpossibleActionException, InexistentModeratorException {

		Entity chat = getChat(chat_id);

		if (!req_email.equals(chat.getString(ADMIN)))
			throw new ImpossibleActionException("This user cannot remove a moderator");

		return removeModerator(chat.getKey(), chat, target_email);
	}
	
	public static List<String> getModerators(String chat_id) throws InexistentChatIdException{
		Entity chat = getChat(chat_id);
		return DB_Util.getStringList(chat, MODS);
	}
	
	private static Entity getChat(String chat_id) throws InexistentChatIdException {
		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException("Inexistent chat id");
		
		return chat;
	}
}
