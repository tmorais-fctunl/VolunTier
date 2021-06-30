package voluntier.util.consumes;

import voluntier.util.userdata.UserData_Modifiable;

public class UploadImageData extends RequestData {

	public String data; // data should be something like 'data:image/png;base64,{base64encoding}'

	public UploadImageData() {
	}

	public UploadImageData(String email, String token, String data) {
		super(email, token);
		this.data = data;
	}

	public boolean isValid() {
		return super.isValid() && UserData_Modifiable.profilePictureValid(data);
	}
}
