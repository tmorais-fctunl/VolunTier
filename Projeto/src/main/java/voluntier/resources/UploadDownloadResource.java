package voluntier.resources;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import voluntier.util.consumes.RequestUploadDownloadData;
import voluntier.util.produces.SignedURLReturn;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UploadDownloadResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson json = new Gson();

	private static Storage storage = StorageOptions.getDefaultInstance().getService();

	public UploadDownloadResource() {
	}

	private Response common(RequestUploadDownloadData data, HttpMethod method) {
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

			return Response.ok(json.toJson(new SignedURLReturn(signedURL))).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/upload/request")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestUploadSignedURL(RequestUploadDownloadData data) {
		return common(data, HttpMethod.PUT);
	}

	@POST
	@Path("/download/request")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestDownloadSignedURL(RequestUploadDownloadData data) {
		return common(data, HttpMethod.GET);
	}
}
