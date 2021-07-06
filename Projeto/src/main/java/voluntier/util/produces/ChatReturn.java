package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

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
			String comment_string = comment.toString();
			String refactor = StringEscapeUtils.unescapeJava(comment_string.substring(1, comment_string.length() - 1));
			System.out.println("YOOOOOOOOOOOOOOOOO " + comment_string  + " " + refactor);
			comments.add(JsonUtil.json.fromJson(refactor, CommentData.class));
		});

	}
}
