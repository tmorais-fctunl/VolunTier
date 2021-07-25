package voluntier.util.produces.causes;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

public class DonatorsDataReturn {
	
	List<DonatorDataReturn> donators;
	public Integer cursor;
	public String results;
	
	public DonatorsDataReturn (List<DonatorDataReturn> donators, Integer cursor, MoreResultsType results) {
		this.donators = donators;
		this.cursor = cursor;
		this.results = results.toString();
	}
}
