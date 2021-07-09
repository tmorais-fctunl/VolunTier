package voluntier.util.eventdata.chatdata;

import java.util.Iterator;
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
import com.google.cloud.datastore.Value;

import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.util.JsonUtil;
import voluntier.util.eventdata.MessageData;

public class DB_MessageLog {
	public static final String MESSAGES = "messages";
	public static final String CHAT_ID = "chat_id";
	public static final String ID = "id";
	public static final String START_INDEX = "start_index";

	public static final int MAX_LOG_SIZE = 1000;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory logFactory = datastore.newKeyFactory().setKind("MessageLog");

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

		return new Pair<>(Entity.newBuilder(idKey).set(ID, idKey.getName()).set(CHAT_ID, chat_id)
				.set(MESSAGES, messages.build()).set(START_INDEX, start_index).build(), idKey.getName());
	}

	private static Entity updateMessages(Key key, Entity old, ListValue messages) {
		return Entity.newBuilder(key).set(ID, old.getString(ID)).set(CHAT_ID, old.getString(CHAT_ID))
				.set(START_INDEX, old.getLong(START_INDEX)).set(MESSAGES, messages).build();
	}

	public static MessageData getMessage(String log_id, int message_id)
			throws InexistentLogIdException, InexistentMessageIdException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();

		List<Value<?>> messages = log.getList(MESSAGES);
		Iterator<Value<?>> it = messages.iterator();
		MessageData out = null;
		while (it.hasNext()) {
			Value<?> message = it.next();
			MessageData message_data = JsonUtil.json.fromJson((String) message.get(), MessageData.class);
			if (message_id == message_data.message_id) {
				out = message_data;
				break;
			}
		}

		if (out == null)
			throw new InexistentMessageIdException();

		return out;
	}

	public static Triplet<Entity, String, Integer> createLogAndAddMessage(String chat_id, int start_index, String email,
			String message) {
		Pair<Entity, String> newLog = createNew(chat_id, start_index);
		String message_log_id = newLog.getValue1();
		Pair<Entity, Integer> updated_log;
		
		updated_log = addMessage(newLog.getValue0().getKey(), newLog.getValue0(), email, message);
		Integer message_id = updated_log.getValue1();

		return new Triplet<>(updated_log.getValue0(), message_log_id, message_id);
	}

	private static Pair<Entity, Integer> addMessage(Key key, Entity log, String email, String message) {
		List<Value<?>> messages = log.getList(MESSAGES);

		ListValue.Builder newList = ListValue.newBuilder().set(messages);
		int message_id = (int) (log.getLong(START_INDEX) + messages.size());
		MessageData message_data = new MessageData(email, message, Timestamp.now().toString(), message_id, 0);

		newList.addValue(JsonUtil.json.toJson(message_data));

		return new Pair<Entity, Integer>(updateMessages(key, log, newList.build()), message_id);
	}

	public static Pair<Entity, Integer> addMessage(String log_id, String email, String message)
			throws InexistentLogIdException, MaximumSizeReachedException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();
		if (Entity.calculateSerializedSize(log) + message.length() > MAX_LOG_SIZE)
			throw new MaximumSizeReachedException();

		return addMessage(idKey, log, email, message);
	}

	public static Entity deleteMessage(String log_id, int message_id)
			throws InexistentLogIdException, InexistentMessageIdException {

		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();

		List<Value<?>> messages = log.getList(MESSAGES);
		ListValue.Builder newList = ListValue.newBuilder();
		Iterator<Value<?>> it = messages.iterator();

		boolean changed = false;
		while (it.hasNext()) {
			Value<?> message = it.next();
			MessageData message_data = JsonUtil.json.fromJson((String) message.get(), MessageData.class);
			if (message_id != message_data.message_id) {
				newList.addValue(message);
			} else
				changed = true;
		}

		if (!changed)
			throw new InexistentMessageIdException("There is no message with id: " + message_id + ".");

		return updateMessages(idKey, log, newList.build());
	}

	public static Entity editMessage(String log_id, int message_id, String new_message)
			throws InexistentLogIdException, InexistentMessageIdException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();

		List<Value<?>> messages = log.getList(MESSAGES);
		ListValue.Builder newList = ListValue.newBuilder();
		Iterator<Value<?>> it = messages.iterator();
		boolean changed = false;

		while (it.hasNext()) {
			Value<?> comment = it.next();
			MessageData comment_data = JsonUtil.json.fromJson((String) comment.get(), MessageData.class);

			if (message_id != comment_data.message_id)
				newList.addValue(comment);
			else {
				comment_data.comment = new_message;
				newList.addValue(JsonUtil.json.toJson(comment_data));
				changed = true;
			}
		}
		if (!changed)
			throw new InexistentMessageIdException("There is no message with id: " + message_id + ".");

		return updateMessages(idKey, log, newList.build());
	}

	public static Entity likeMessage(String log_id, int message_id)
			throws InexistentMessageIdException, InexistentLogIdException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();

		List<Value<?>> messages = log.getList(MESSAGES);
		ListValue.Builder newList = ListValue.newBuilder();
		Iterator<Value<?>> it = messages.iterator();
		boolean changed = false;

		while (it.hasNext()) {
			Value<?> comment = it.next();
			MessageData comment_data = JsonUtil.json.fromJson((String) comment.get(), MessageData.class);

			if (message_id != comment_data.message_id)
				newList.addValue(comment);
			else {
				comment_data.likes += 1;
				newList.addValue(JsonUtil.json.toJson(comment_data));
				changed = true;
			}
		}
		if (!changed)
			throw new InexistentMessageIdException("There is no message with id: " + message_id + ".");

		return updateMessages(idKey, log, newList.build());
	}

	public static List<MessageData> getMessages(String log_id) throws InexistentLogIdException {
		Key idKey = logFactory.newKey(log_id);
		Entity log = datastore.get(idKey);

		if (log == null)
			throw new InexistentLogIdException();

		List<Value<?>> messages = log.getList(MESSAGES);
		List<MessageData> out = new LinkedList<>();
		messages.forEach(message -> {
			MessageData message_data = JsonUtil.json.fromJson((String) message.get(), MessageData.class);
			out.add(message_data);
		});

		return out;
	}

}
