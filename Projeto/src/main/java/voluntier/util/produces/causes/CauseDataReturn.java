package voluntier.util.produces.causes;

import com.google.cloud.datastore.Entity;

import voluntier.util.data.causes.DB_Cause;
import voluntier.util.produces.pictures.PicturesReturn;

public class CauseDataReturn extends PicturesReturn {
	
	public String id;
	public String name;
	public Integer goal;
	public Double raised;
	public Integer num_donations;
	public String website;
	public String description;
	public String company_name;
	
	public CauseDataReturn () {
	}
	
	public CauseDataReturn (Entity cause) {
		super(DB_Cause.getImagesDownloadURLs(cause));
		
		this.id = cause.getString(DB_Cause.ID);
		this.name = cause.getString(DB_Cause.NAME);
		this.goal = (int) cause.getLong(DB_Cause.GOAL);
		this.raised = cause.getDouble(DB_Cause.RAISED);
		this.description = cause.getString(DB_Cause.DESCRIPTION);
		this.website = cause.getString(DB_Cause.WEBSITE);
		this.company_name = cause.getString(DB_Cause.WITH);
		this.num_donations = cause.getList(DB_Cause.DONATORS).size();
	}
}
