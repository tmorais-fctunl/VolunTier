package voluntier.util.consumes.causes;

public class DonatorsDataConsumes extends CauseData {
	public Integer cursor;
	
	public DonatorsDataConsumes() {
	}
	
	public DonatorsDataConsumes(String email, String token, String cause_id, Integer cursor) {
		super(email, token, cause_id);
		this.cursor = cursor;
	}
	
	public boolean isValid() {
		return super.isValid();
	}
}
