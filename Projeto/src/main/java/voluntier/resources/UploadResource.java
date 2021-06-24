package voluntier.resources;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import voluntier.util.consumes.RequestData;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UploadResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson json = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");

	private static Storage storage = StorageOptions.getDefaultInstance().getService();

	public UploadResource() {
	}

	@POST
	@Path("/user/upload")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(@HeaderParam("Content-Type") String contentType, RequestData data) {

		String bucketName = "my-unique-bucket";
		String fileName = "";
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

		URL signedURL = storage.signUrl(blobInfo, 1, TimeUnit.HOURS, Storage.SignUrlOption.httpMethod(HttpMethod.POST));

		return Response.ok(signedURL).build();
	}
}
