package voluntier.resources;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import voluntier.util.consumes.ChangePassData;
import voluntier.util.consumes.ForgotPassData;
import voluntier.util.email.ChangePasswordEmail;
import voluntier.util.email.ForgotData;
import voluntier.util.userdata.Account;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.State;

import java.time.Duration;
import java.time.Instant;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

@Path("/forgotpassword")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ForgotPassResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory serviceEmailFactory = datastore.newKeyFactory().setKind("ServiceEmail");
	private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");

	private static final String htmlContent = "<html><head><style>iframe[seamless]{border: none;}</style></head>"
			+ "<body><iframe src=\"https://voluntier-312115.ew.r.appspot.com/www/pages/pass/change_pass.html\" "
			+ "seamless=\"\"></iframe></body></html>";

	public ForgotPassResource() {
	}

	@GET
	@Path("/test")
	// @Produces(MediaType)
	public Response test() {

		String password = "Hello World!";
		Instant beginHash = Instant.now();

		Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);

		String hash = argon2.hash(10, 8192, 1, password);

		Instant endHash = Instant.now();

		String hashing = String.format("Process took %f s", Duration.between(beginHash, endHash).toMillis() / 1024.0);

		Instant beginVerify = Instant.now();

		boolean success = argon2.verify(hash, password);

		Instant endVerify = Instant.now();

		String verifying = String.format("Process took %f s",
				Duration.between(beginVerify, endVerify).toMillis() / 1024.0);

		return Response.ok("HASH: " + hash + " HASHING: " + hashing + " VERIFYING: " + verifying).build();
	}

	@POST
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePass(@QueryParam("t") String code, ChangePassData data) {
		Transaction txn = datastore.newTransaction();

		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);

			if (confirmation == null
					|| confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid confirmation url").build();
			} else {

				String user_id = confirmation.getString("user_id");

				Key userKey = usersFactory.newKey(user_id);
				Entity user = txn.get(userKey);

				if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User does not exist: " + user_id).build();
				} else {

					if (!data.isValid()) {
						txn.rollback();
						return Response.status(Status.BAD_REQUEST).entity("Passwords don't match").build();
					}

					user = DB_User.changePassword(data.password, userKey, user);

					SessionResource.invalidateAllSessionsOfUser(user_id, txn);

					confirmation = Entity.newBuilder(confirmationKey)
							.set("confirmation_code", confirmation.getString("confirmation_code"))
							.set("user_id", confirmation.getString("user_id"))
							.set("confirmation_email", confirmation.getString("confirmation_email"))
							.set("confirmation_creation_date", confirmation.getLong("confirmation_expiration_date"))
							.set("confirmation_expiration_date", System.currentTimeMillis()).build();

					txn.put(user, confirmation);
					txn.commit();

					LOG.fine("User: " + user_id + " changed password.");
					return Response.status(Status.NO_CONTENT).build();
				}

			}

		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@GET
	@Path("/confirm")
	public Response doConfirmation(@QueryParam("t") String code) {
		Transaction txn = datastore.newTransaction();

		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);

			if (confirmation == null
					|| confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid confirmation url").build();
			} else {

				txn.rollback();
				return Response.ok(htmlContent, MediaType.TEXT_HTML).build();
			}

		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forgotPass(ForgotPassData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(data.user_id);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User does not exist: " + data.user_id).build();
			} else {

				if (!user.getString(DB_User.EMAIL).equals(data.email)) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User email does not match the email given.")
							.build();
				}

				Key emailKey = serviceEmailFactory.newKey("confirmation-email");
				Entity serviceEmail = txn.get(emailKey);

				if (serviceEmail != null) {
					try {
						ForgotData url = ChangePasswordEmail.sendConfirmationEmail(serviceEmail.getString("email"),
								data);

						Key confirmationKey = confirmationFactory.newKey(url.code);
						Entity confirmation = Entity
								.newBuilder(confirmationKey).set("confirmation_code", url.code).set("user_id",
										url.user_id)
								.set("confirmation_email", url.email)/* .set("pwd", url.password) */
								.set("confirmation_creation_date", url.creationDate)
								.set("confirmation_expiration_date", url.expirationDate).build();

						txn.add(confirmation);
						txn.commit();

						LOG.fine("Confirmation url generated: " + url.code);
						return Response.status(Status.NO_CONTENT).build();
					} catch (MessagingException e) {
						txn.rollback();
						LOG.severe("Something went wrong while sending confirmation email.");
						return Response.status(Status.BAD_GATEWAY).build();
					}
				} else {
					txn.rollback();
					LOG.severe("Something went wrong while retrieving information about the service email.");
					return Response.status(Status.SERVICE_UNAVAILABLE).build();
				}
			}
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

}
