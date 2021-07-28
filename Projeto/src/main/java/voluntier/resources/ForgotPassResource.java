package voluntier.resources;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

import voluntier.util.consumes.user.ChangePassData;
import voluntier.util.consumes.user.ForgotPassData;
import voluntier.util.data.user.DB_User;
import voluntier.util.email.ChangePasswordEmail;
import voluntier.util.email.ForgotData;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ForgotPassResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory serviceEmailFactory = datastore.newKeyFactory().setKind("ServiceEmail");
	private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");

	public ForgotPassResource() {
	}

	@POST
	@Path("/forgotpassword/change")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePass(@QueryParam("t") String code, ChangePassData data) {

		if (!data.isValid() || code == null) {
			LOG.fine("Bad request while trying to change password t=" + code);
			return Response.status(Status.BAD_REQUEST).build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);

			if (confirmation == null
					|| confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid confirmation url").build();
			} else {

				String user_email = confirmation.getString("confirmation_email");

				Key userKey = usersFactory.newKey(user_email);
				Entity user = txn.get(userKey);

				if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User does not exist: " + user_email).build();
				} else {

					user = DB_User.changePassword(data.password, userKey, user);

					SessionResource.invalidateAllSessionsOfUser(user_email, txn);

					confirmation = Entity.newBuilder(confirmationKey)
							.set("confirmation_code", confirmation.getString("confirmation_code"))
							.set("confirmation_email", confirmation.getString("confirmation_email"))
							.set("confirmation_creation_date", confirmation.getLong("confirmation_expiration_date"))
							.set("confirmation_expiration_date", System.currentTimeMillis()).build();

					txn.put(user, confirmation);
					txn.commit();

					LOG.fine("User: " + user_email + " changed password.");
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
	@Path("/forgotpassword/confirm")
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

				// String htmlContent = "<html><head><style>iframe[seamless]{border:
				// none;}</style></head>"
				// + "<body><iframe
				// src=\"https://voluntier-312115.ew.r.appspot.com/pages/changepwd.html?t=" +
				// code + "\" "
				// + "style=\"position:fixed; top:0; left:0; bottom:0; right:0; width:100%; "
				// + "height:100%; border:none; margin:0; padding:0; "
				// + "overflow:hidden; z-index:999999;\" seamless=\"\"></iframe></body></html>";
				String htmlContent = "<meta http-equiv=\"refresh\" "
						+ "content=\"0;URL=https://voluntier-317915.ew.r.appspot.com/pages/changepwd.html?t=" + code + "\" />";

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
	@Path("/forgotpassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forgotPass(ForgotPassData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User does not exist: " + data.email).build();
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
						Entity confirmation = Entity.newBuilder(confirmationKey).set("confirmation_code", url.code)
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
