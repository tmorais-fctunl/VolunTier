package voluntier.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
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
	public Response upload(RequestData data) throws UnsupportedEncodingException, IOException {

		String bucketName = "voluntier-317915.appspot.com";
		String fileName = "dwld.txt";
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

		URL signedURL = storage.signUrl(blobInfo, 1, TimeUnit.HOURS, Storage.SignUrlOption.httpMethod(HttpMethod.PUT));

		//storage.create(blobInfo, "Hrello".getBytes("UTF-8"));
		
		//storage.writer(signedURL).write(ByteBuffer.wrap("--cookies youtube.com_cookies.txt".getBytes("UTF-8")));

		return Response.ok(signedURL).build();
	}
}
