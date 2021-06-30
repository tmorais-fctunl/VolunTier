package voluntier.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GoogleStorageUtil {
	private static Storage storage = StorageOptions.getDefaultInstance().getService();
	
	private static URL common(String filename, HttpMethod method) {
		String bucketName = "voluntier-317915.appspot.com";
		String fileName = filename;
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

		//long maxContentLength = 1048576; // 1Mb
		//Map<String, String> extHeaders = new HashMap<>();
		//extHeaders.put("x-goog-content-length-range", "0," + maxContentLength);

		URL signedURL = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES,
				Storage.SignUrlOption.httpMethod(method)/*, Storage.SignUrlOption.withExtHeaders(extHeaders)*/);
		
		return signedURL;
	}
	
	public static URL signURLForUpload(String filename) {
		return common(filename, HttpMethod.PUT);
	}
	
	public static URL signURLForDownload(String filename) {
		return common(filename, HttpMethod.GET);
	}
}
