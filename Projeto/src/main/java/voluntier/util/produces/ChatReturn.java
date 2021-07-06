package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Value;

import voluntier.util.JsonUtil;
import voluntier.util.eventdata.CommentData;

public class ChatReturn {
	
	public List<CommentData> comments;
	
	public ChatReturn () {
	}
	
	public ChatReturn (List<Value<?>> chat) {
		comments = new LinkedList<CommentData>();
		chat.forEach(comment -> {
			comments.add(JsonUtil.json.fromJson(comment.toString(), CommentData.class));
		});
	}
}
