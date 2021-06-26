package voluntier.resources;

import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import voluntier.util.AuthToken;

public class TokensResource {
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory refreshFactory = datastore.newKeyFactory().setKind("RefreshSession");

	public static final String REFESH_TOKEN = "refresh_token";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFESH_EXPIRATION = "refresh_expiration_date";
	public static final String REFRESH_CREATION = "refresh_creation_date";
	public static final String ACCESS_EXPIRATION = "token_expiration_date";
	public static final String ACCESS_CREATION = "token_creation_date";
	public static final String ACCESS_EMAIL = "token_user_email";
	public static final String REFESH_EMAIL = "refresh_user_email";
	
	public static Entity invalidateRefreshToken(Entity refresh, Key refreshKey) {
		return Entity.newBuilder(refreshKey)
				.set(REFESH_TOKEN, refresh.getString(REFESH_TOKEN))
				.set(ACCESS_TOKEN, refresh.getString(ACCESS_TOKEN))
				.set(REFESH_EXPIRATION, System.currentTimeMillis())
				.set(REFESH_EMAIL, refresh.getString(REFESH_EMAIL))
				.set(REFRESH_CREATION, refresh.getLong(REFRESH_CREATION))
				.build();
	}

	public static Entity invalidateAccessToken(Entity token, Key tokenKey) {
		return Entity.newBuilder(tokenKey)
				.set(REFESH_TOKEN, token.getString(REFESH_TOKEN))
				.set(ACCESS_TOKEN, token.getString(ACCESS_TOKEN))
				.set(ACCESS_EXPIRATION, System.currentTimeMillis())
				.set(ACCESS_EMAIL, token.getString(ACCESS_EMAIL))
				.set(ACCESS_CREATION, token.getLong(ACCESS_CREATION))
				.build();
	}

	public static Triplet<Entity, Entity, AuthToken> createNewAccessAndRefreshTokens(String email){
		AuthToken authToken = new AuthToken(email);

		Key refreshKey = refreshFactory.newKey(authToken.refreshToken);
		Entity refresh = Entity.newBuilder(refreshKey)
				.set(REFESH_TOKEN, authToken.refreshToken)
				.set(ACCESS_TOKEN, authToken.accessToken)
				.set(REFESH_EXPIRATION, authToken.refresh_expirationDate)
				.set(REFESH_EMAIL, authToken.email)
				.set(REFRESH_CREATION, authToken.creationDate)
				.build();

		Key tokenKey = sessionFactory.newKey(authToken.accessToken);
		Entity token = Entity.newBuilder(tokenKey)
				.set(ACCESS_TOKEN, authToken.accessToken)
				.set(REFESH_TOKEN, authToken.refreshToken)
				.set(ACCESS_EMAIL, authToken.email)
				.set(ACCESS_CREATION, authToken.creationDate)
				.set(ACCESS_EXPIRATION, authToken.expirationDate)
				.build();

		return new Triplet<>(refresh, token, authToken);
	}
	
	public static boolean isValidAccess(Entity token) {
		return token != null && token.getLong(ACCESS_EXPIRATION) > System.currentTimeMillis();
	}
	
	public static boolean isValidAccess(Entity token, String email) {
		return isValidAccess(token) && token.getString(ACCESS_EMAIL).equals(email);
	}
	
	public static boolean isValidAccess(String tokenString, String email) {
		Key tokenKey = sessionFactory.newKey(tokenString);
		Entity token = datastore.get(tokenKey);
		
		return isValidAccess(token, email);
	}
	
	public static boolean isValidRefresh(Entity token) {
		return token != null && token.getLong(REFESH_EXPIRATION) > System.currentTimeMillis();
	}
	
	public static boolean isValidRefresh(Entity token, String email) {
		return isValidRefresh(token) && token.getString(REFESH_EMAIL).equals(email);
	}
}
