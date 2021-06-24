package voluntier.util.consumes;

public class SearchUserData extends RequestData{
	
	public String[] cursor;
	
	public SearchUserData() {}
	public SearchUserData(String email, String token, String[] cursor) {
		super(email, token);
		this.cursor = cursor;
	}
	
	public boolean isValid() {
		return (cursor == null || (cursor.length == 2 && !cursor[0].equals("") && !cursor[1].equals(""))) && super.isValid();
	}
}
