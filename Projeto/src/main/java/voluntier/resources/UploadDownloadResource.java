package voluntier.resources;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import voluntier.util.JsonUtil;
import voluntier.util.consumes.RequestUploadDownloadData;
import voluntier.util.consumes.UploadImageData;
import voluntier.util.produces.DownloadSignedURLReturn;
import voluntier.util.produces.UploadSignedURLReturn;

@Path("/request")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UploadDownloadResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private static Storage storage = StorageOptions.getDefaultInstance().getService();

	public UploadDownloadResource() {
	}

	/*private Response common(RequestUploadDownloadData data, HttpMethod method) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		try {
			// check if the token corresponds to the user received and hasnt expired yet
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed request to get a signed URL attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			String bucketName = "voluntier-317915.appspot.com";
			String fileName = data.filename;
			BlobId blobId = BlobId.of(bucketName, fileName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

			URL signedURL = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.httpMethod(method));

			if (method == HttpMethod.PUT)
				return Response.ok(json.toJson(new UploadSignedURLReturn(signedURL))).build();
			else if (method == HttpMethod.GET) {
				Blob obj = storage.get(blobId);
				if (obj != null) {
					long size = obj.getSize();
					return Response.ok(json.toJson(new DownloadSignedURLReturn(signedURL, size))).build();
				} else
					return Response.status(Status.NOT_FOUND).build();
			}

			return Response.status(Status.BAD_REQUEST).build();
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}*/

	BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();
		return resizedImage;
	}

	@POST
	@Path("/v2/upload/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestUploadSignedURL(UploadImageData data) throws IOException {

		//if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		//InputStream is = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(data.getBase64Data()));
		//BufferedImage newBi = resizeImage(ImageIO.read(is), 100, 100);

		//ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//ImageIO.write(newBi, data.getImageType(), bos);
		//byte[] d = bos.toByteArray();
		//return Response.ok(DatatypeConverter.printBase64Binary(d)).build();

	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestUploadSignedURL(RequestUploadDownloadData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		try {
			// check if the token corresponds to the user received and hasnt expired yet
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed request to get a signed URL attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			String bucketName = "voluntier-317915.appspot.com";
			String fileName = data.filename;
			BlobId blobId = BlobId.of(bucketName, fileName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

			long maxContentLength = 1048576; // 1Mb
			Map<String, String> extHeaders = new HashMap<>();
			extHeaders.put("x-goog-content-length-range", "0," + maxContentLength);

			URL signedURL = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES,
					Storage.SignUrlOption.httpMethod(HttpMethod.PUT), Storage.SignUrlOption.withExtHeaders(extHeaders));

			return Response.ok(JsonUtil.json.toJson(new UploadSignedURLReturn(signedURL))).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestDownloadSignedURL(RequestUploadDownloadData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		try {
			// check if the token corresponds to the user received and hasnt expired yet
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed request to get a signed URL attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			String bucketName = "voluntier-317915.appspot.com";
			String fileName = data.filename;
			BlobId blobId = BlobId.of(bucketName, fileName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

			URL signedURL = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES,
					Storage.SignUrlOption.httpMethod(HttpMethod.GET));

			Blob obj = storage.get(blobId);
			if (obj != null) {
				long size = obj.getSize();
				return Response.ok(JsonUtil.json.toJson(new DownloadSignedURLReturn(signedURL, size))).build();
			} else
				return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
