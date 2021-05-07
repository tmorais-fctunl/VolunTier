package resources;

import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import util.AuthToken;

public class TokensResource {
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory refreshFactory = datastore.newKeyFactory().setKind("RefreshSession");
	
	public static Entity invalidateRefreshToken(Entity refresh, Key refreshKey) {
		return Entity.newBuilder(refreshKey)
				.set("refresh_token", refresh.getString("refresh_token"))
				.set("access_token", refresh.getString("access_token"))
				.set("refresh_expiration_date", System.currentTimeMillis())
				.set("refresh_user_id", refresh.getString("refresh_user_id"))
				.set("refresh_creation_date", refresh.getLong("refresh_creation_date"))
				.build();
	}

	public static Entity invalidateAccessToken(Entity token, Key tokenKey) {
		return Entity.newBuilder(tokenKey)
				.set("refresh_token", token.getString("refresh_token"))
				.set("access_token", token.getString("access_token"))
				.set("token_expiration_date", System.currentTimeMillis())
				.set("token_user_id", token.getString("token_user_id"))
				.set("token_creation_date", token.getLong("token_creation_date"))
				.build();
	}

	public static Triplet<Entity, Entity, AuthToken> createNewAccessAndRefreshTokens(String user_id){
		AuthToken authToken = new AuthToken(user_id);

		Key refreshKey = refreshFactory.newKey(authToken.refreshToken);
		Entity refresh = Entity.newBuilder(refreshKey)
				.set("refresh_token", authToken.refreshToken)
				.set("access_token", authToken.accessToken)
				.set("refresh_expiration_date", authToken.refresh_expirationDate)
				.set("refresh_user_id", authToken.user_id)
				.set("refresh_creation_date", authToken.creationDate)
				.build();

		Key tokenKey = sessionFactory.newKey(authToken.accessToken);
		Entity token = Entity.newBuilder(tokenKey)
				.set("access_token", authToken.accessToken)
				.set("refresh_token", authToken.refreshToken)
				.set("token_user_id", authToken.user_id)
				.set("token_creation_date", authToken.creationDate)
				.set("token_expiration_date", authToken.expirationDate)
				.build();

		return new Triplet<>(refresh, token, authToken);
	}
	
	public static boolean isValidAccess(Entity token) {
		return token != null && token.getLong("token_expiration_date") > System.currentTimeMillis();
	}
	
	public static boolean isValidAccess(Entity token, String user_id) {
		return isValidAccess(token) && token.getString("token_user_id").equals(user_id);
	}
	
	public static boolean isValidRefresh(Entity token) {
		return token != null && token.getLong("refresh_expiration_date") > System.currentTimeMillis();
	}
	
	public static boolean isValidRefresh(Entity token, String user_id) {
		return isValidRefresh(token) && token.getString("refresh_user_id").equals(user_id);
	}
}
