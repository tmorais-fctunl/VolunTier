package voluntier.util.userdata;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ProfilePicture{

	private static final String HEADER_REGEX = "data[:]image[/](png|jpg|jpeg)[;]base64";
	public String data; // data should be something like 'data:image/png;base64,{base64encoding}'
	private String base64encoding;
	private String header;
	private BufferedImage image;

	public ProfilePicture() {
	}

	public ProfilePicture(String data) {
		this.data = data;
	}

	private void processData() throws IOException {
		String[] d = data.split(",");
		header = d[0];
		base64encoding = d[1];
		InputStream is = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(getBase64Data()));
		image = ImageIO.read(is);
	}

	public boolean isValid() {
		if (data == null || data.equals(""))
			return false;

		try {
			processData();
			return header.matches(HEADER_REGEX) && image.getHeight() == 200
					&& image.getWidth() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	public BufferedImage getImage() {
		try {
			if(image == null) processData();
		} catch (IOException e) {}
		return image;
	}

	public String getBase64Data() {
		try {
			if(base64encoding == null) processData();
		} catch (IOException e) {}
		return base64encoding;
	}

	public String getImageType() {
		try {
			if(header == null) processData();
		} catch (IOException e) {}
		return header;
	}
}
