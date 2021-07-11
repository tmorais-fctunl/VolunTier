package voluntier.util.produces;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.MessageData;

public class ChatReturn {
	
	public List<MessageData> comments;
	public Integer cursor;
	public String results;

	public ChatReturn () {
	}
	
	public ChatReturn (List<MessageData> chat, Integer cursor, MoreResultsType more_results) {
		this.comments = chat;
		this.cursor = cursor;
		this.results = more_results.toString();
	}
}
