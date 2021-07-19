package voluntier.util.produces;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.MessageDataReturn;

public class ChatReturn {
	
	public List<MessageDataReturn> comments;
	public Integer cursor;
	public String results;

	public ChatReturn () {
	}
	
	public ChatReturn (List<MessageDataReturn> chat, Integer cursor, MoreResultsType more_results) {
		this.comments = chat;
		this.cursor = cursor;
		this.results = more_results.toString();
	}
}
