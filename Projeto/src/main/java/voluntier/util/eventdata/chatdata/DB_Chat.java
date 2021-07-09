package voluntier.util.eventdata.chatdata;

import java.util.Iterator;
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
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.JsonUtil;
import voluntier.util.eventdata.MessageData;

public class DB_Chat {
	public static final String MLs = "chat_message_logs";
	public static final String MODS = "chat_moderators";
	public static final String ADMIN = "chat_admin";
	public static final String ID = "chat_id";

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory chatFactory = datastore.newKeyFactory().setKind("Chat");

	private static Key generateChatID() {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Chat" + rand.nextInt();
			idKey = chatFactory.newKey(id);
			chatFactory.newKey(id);
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
		ents.add(Entity.newBuilder(idKey).set(ID, idKey.getName()).set(MODS, moderators.build())
				.set(MLs, message_logs.build()).set(ADMIN, admin_email).build());
		ents.add(message_log.getValue0());

		return new Pair<>(ents, idKey.getName());
	}

	private static Entity addMessageLog(Key key, Entity chat, MessageLog data) {
		List<Value<?>> messages = chat.getList(MLs);

		ListValue.Builder newList = ListValue.newBuilder().set(messages);
		newList.addValue(JsonUtil.json.toJson(data));

		return Entity.newBuilder(key).set(ID, chat.getString(ID)).set(MODS, chat.getList(MODS))
				.set(MLs, newList.build()).set(ADMIN, chat.getString(ADMIN)).build();
	}

	private static Entity updateModeratorList(Key key, Entity chat, ListValue list) {
		return Entity.newBuilder(key).set(ID, chat.getString(ID)).set(MODS, list).set(MLs, chat.getList(MLs))
				.set(ADMIN, chat.getString(ADMIN)).build();
	}

	private static Entity addModerator(Key key, Entity chat, String email) {
		List<Value<?>> mods = chat.getList(MODS);

		ListValue.Builder newList = ListValue.newBuilder().set(mods);
		newList.addValue(email);

		return updateModeratorList(key, chat, newList.build());
	}

	private static Entity removeModerator(Key key, Entity chat, String email) throws InexistentModeratorException {

		List<Value<?>> mods = chat.getList(MODS);
		ListValue.Builder newList = ListValue.newBuilder();
		Iterator<Value<?>> it = mods.iterator();

		boolean changed = false;
		while (it.hasNext()) {
			Value<?> mod = it.next();
			String mod_email = (String) mod.get();
			if (!email.equals(mod_email)) {
				newList.addValue(mod);
			} else
				changed = true;
		}

		if (!changed)
			throw new InexistentModeratorException();

		return updateModeratorList(key, chat, newList.build());
	}

	private static MessageLog getLastMessageLog(Entity chat) {
		List<Value<?>> message_logs = chat.getList(MLs);
		Value<?> last_log = message_logs.get(message_logs.size() - 1);
		MessageLog last_log_data = JsonUtil.json.fromJson((String) last_log.get(), MessageLog.class);

		return last_log_data;
	}

	private static List<MessageLog> getMessageLogs(Entity chat) {
		List<Value<?>> message_logs = chat.getList(MLs);
		List<MessageLog> logs = new LinkedList<>();
		message_logs.forEach(log -> logs.add(JsonUtil.json.fromJson((String) log.get(), MessageLog.class)));

		return logs;
	}

	private static List<String> getModeratorList(Entity chat) {
		List<Value<?>> moderators = chat.getList(MODS);
		List<String> mods = new LinkedList<>();
		moderators.forEach(mod -> mods.add((String) mod.get()));

		return mods;
	}

	public static Pair<List<Entity>, Integer> postMessage(String chat_id, String email, String message)
			throws InexistentChatIdException, InexistentLogIdException, SomethingWrongException {
		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		List<Entity> ents = new LinkedList<>();

		// get the last MessageLog on the list.
		MessageLog last_log_data = getLastMessageLog(chat);

		String last_log_id = last_log_data.id;
		int message_id = -1;

		try {
			Pair<Entity, Integer> message_log = DB_MessageLog.addMessage(last_log_id, email, message);
			ents.add(message_log.getValue0());
			message_id = message_log.getValue1();

		} catch (MaximumSizeReachedException e) {
			int last_start_index = last_log_data.start_index;
			// need to create a new MessageLog and add it to current list with the message already added
			int new_start_index = last_start_index + DB_MessageLog.getMessages(last_log_id).size();

			Triplet<Entity, String, Integer> new_message_log = DB_MessageLog.createLogAndAddMessage(chat_id,
					new_start_index, email, message);

			String message_log_id = new_message_log.getValue1();
			message_id = new_message_log.getValue2();

			Entity newChatMessageLogs = addMessageLog(idKey, chat, new MessageLog(message_log_id, new_start_index));
			ents.add(newChatMessageLogs);

			ents.add(new_message_log.getValue0());
		}

		return new Pair<>(ents, message_id);
	}

	public static Entity deleteMessage(String chat_id, int message_id, String email) throws InexistentChatIdException,
			InexistentLogIdException, InexistentMessageIdException, ImpossibleActionException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		MessageLog last_log_data = getLastMessageLog(chat);

		MessageData message = DB_MessageLog.getMessage(last_log_data.id, message_id);

		List<String> mods = getModeratorList(chat);
		if (!message.email.equals(email) && !mods.contains(email))
			throw new ImpossibleActionException();

		Entity newMessageLog = DB_MessageLog.deleteMessage(last_log_data.id, message_id);

		return newMessageLog;
	}

	public static Entity editMessage(String chat_id, int message_id, String email, String new_message)
			throws InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException,
			ImpossibleActionException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		MessageLog last_log_data = getLastMessageLog(chat);

		MessageData message = DB_MessageLog.getMessage(last_log_data.id, message_id);
		if (!message.email.equals(email))
			throw new ImpossibleActionException();

		Entity newMessageLog = DB_MessageLog.editMessage(last_log_data.id, message_id, new_message);

		return newMessageLog;
	}

	public static Entity likeMessage(String chat_id, int message_id, String new_message)
			throws InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		MessageLog last_log_data = getLastMessageLog(chat);

		Entity newMessageLog = DB_MessageLog.editMessage(last_log_data.id, message_id, new_message);

		return newMessageLog;
	}

	public static Triplet<List<MessageData>, Integer, QueryResultBatch.MoreResultsType> getChat(String chat_id,
			int cursor) throws InexistentChatIdException, InvalidCursorException, InexistentLogIdException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		List<MessageLog> logs = getMessageLogs(chat);

		if (cursor > logs.size() || cursor < 0)
			throw new InvalidCursorException();

		MessageLog log = logs.get(cursor);
		boolean more_results = cursor < logs.size() - 1;

		return new Triplet<>(DB_MessageLog.getMessages(log.id), more_results ? cursor + 1 : null,
				more_results ? MoreResultsType.MORE_RESULTS_AFTER_LIMIT : MoreResultsType.NO_MORE_RESULTS);
	}

	public static Entity makeModerator(String chat_id, String target_email, String req_email)
			throws InexistentChatIdException, ImpossibleActionException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		if (!req_email.equals(chat.getString(ADMIN)))
			throw new ImpossibleActionException();

		return addModerator(idKey, chat, target_email);
	}

	public static Entity removeModerator(String chat_id, String target_email, String req_email)
			throws InexistentChatIdException, ImpossibleActionException, InexistentModeratorException {

		Key idKey = chatFactory.newKey(chat_id);
		Entity chat = datastore.get(idKey);

		if (chat == null)
			throw new InexistentChatIdException();

		if (!req_email.equals(chat.getString(ADMIN)))
			throw new ImpossibleActionException();

		return removeModerator(idKey, chat, target_email);
	}

}
