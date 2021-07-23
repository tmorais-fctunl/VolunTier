package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Triplet;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Entity;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.DB_Event;

public class SearchEventData {
	
	public String cursor;
	public String results;
	
	List<SearchEventReturn> events;
	
	public SearchEventData (Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> data) {
		List<Entity> entities = data.getValue0();
		events = new LinkedList<>();
		entities.forEach(entity -> {
			//String pic = entity.getString();
			events.add(new SearchEventReturn(entity.getString(DB_Event.NAME), entity.getString(DB_Event.ID),
					entity.getString(DB_Event.START_DATE), entity.getString(DB_Event.END_DATE),
					(int) entity.getLong(DB_Event.N_PARTICIPANTS), entity.getString(DB_Event.PROFILE)) );
		});
		
		results = data.getValue2().toString();
		
		if (data.getValue2() != MoreResultsType.NO_MORE_RESULTS)
			cursor = data.getValue1().toUrlSafe();
	}

}
