package voluntier.util;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.javatuples.Pair;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

//import com.google.cloud.storage.Bucket;
//import com.google.cloud.storage.Cors;
//import com.google.common.collect.ImmutableList;


public class GoogleStorageUtil {
	
	
	/*public static void configureBucketCors(String bucketName, String responseHeader,
			Integer maxAgeSeconds) {
		//https://cloud.google.com/storage/docs/configuring-cors#storage_cors_configuration-java

		Bucket bucket = storage.get(bucketName);
		
		Cors cors = Cors.newBuilder().setOrigins(ImmutableList.of(Cors.Origin.of("https://voluntier-317915.appspot.com"),Cors.Origin.of("https://voluntier-317915.ew.r.appspot.com")))
				.setMethods(ImmutableList.of(HttpMethod.GET, HttpMethod.PUT)).setResponseHeaders(ImmutableList.of(responseHeader))
				.setMaxAgeSeconds(maxAgeSeconds).build();

		bucket.toBuilder().setCors(ImmutableList.of(cors)).build().update();

	}*/
		
	
	
	private static Storage storage = StorageOptions.getDefaultInstance().getService();
	
	private static BlobInfo getBlobInfo(String filename) {
		String bucketName = "voluntier-317915.appspot.com";
		String fileName = filename;
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		
		return blobInfo;
	}
	
	private static Pair<URL, Long> common(String filename, HttpMethod method) {
		
		BlobInfo blobInfo = getBlobInfo(filename);
		//configureBucketCors(bucketName, "*", 15*60);

		URL signedURL = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES,
				Storage.SignUrlOption.httpMethod(method)/*, Storage.SignUrlOption.withExtHeaders(extHeaders)*/);
		
		long size = 0;
		Blob obj = storage.get(blobInfo.getBlobId());
		if (obj != null)
			size = obj.getSize();
		
		return new Pair<>(signedURL, size);
	}
	
	public static URL signURLForUpload(String filename) {
		return common(filename, HttpMethod.PUT).getValue0();
	}
	
	public static Pair<URL, Long> signURLForDownload(String filename) {
		return common(filename, HttpMethod.GET);
	}
	
	public static boolean removeFile(String filename) {
		BlobInfo blobInfo = getBlobInfo(filename);		
		return storage.delete(blobInfo.getBlobId());
	}
}
