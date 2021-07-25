package voluntier.util.consumes.causes;

import voluntier.util.consumes.generic.RequestData;

public class CreateCauseData extends RequestData {
	public String cause_name;
	public Integer cause_goal;
	public Integer num_images;
	public String cause_website;
	public String description;
	public String company_name;

	public CreateCauseData() {
	}

	public CreateCauseData(String email, String token, String cause_name, int cause_goal, int num_images,
			String cause_website, String description, String company_name) {
		super(email, token);
		this.cause_name = cause_name;
		this.cause_goal = cause_goal;
		this.num_images = num_images;
		this.cause_website = cause_website;
		this.description = description;
		this.company_name = company_name;
	}

	public boolean isValid() {
		return super.isValid() && cause_name != null && cause_goal != null && num_images != null
				&& cause_website != null && description != null;
	}
}
