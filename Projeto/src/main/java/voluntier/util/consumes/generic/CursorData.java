package voluntier.util.consumes.generic;

public class CursorData extends RequestData{
	
	public String cursor;
	
	public CursorData () {
	}
	
	public CursorData (String email, String token, String cursor) {
		super(email, token);
		this.cursor = cursor;
	}

	public boolean isValid() {
		return (cursor == null || !cursor.equals("") ) && super.isValid() ;
	}
}
