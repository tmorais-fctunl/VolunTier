package voluntier.resources;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;

import voluntier.util.email.ConfirmationData;
import voluntier.util.consumes.user.RegisterData;
import voluntier.util.data.user.DB_User;
import voluntier.util.email.ConfirmRegistrationEmail;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory serviceEmailFactory = datastore.newKeyFactory().setKind("ServiceEmail");
	private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");
	private static KeyFactory usernamesFactory = datastore.newKeyFactory().setKind("ID");

	public RegisterResource() {
	}

	@GET
	@Path("/{code}/confirm")
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response doConfirmation(@PathParam("code") String code) {
		Transaction txn = datastore.newTransaction();
		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);

			if (confirmation == null
					|| confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid url").build();
			} else {

				String user_email = confirmation.getString("confirmation_email");

				Key userKey = usersFactory.newKey(user_email);
				Entity user = txn.get(userKey);
				
				if (user != null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("Email already in use: " + user_email).build();
				} else {

					String username = confirmation.getString("confirmation_username");
					Key usernameKey = usernamesFactory.newKey(username);
					Entity usernameEnt = txn.get(usernameKey);
					
					if(usernameEnt != null) {
						txn.rollback();
						return Response.status(Status.FORBIDDEN).entity("Username already in use: " + username).build();
					}

					usernameEnt = DB_User.createID(username, user_email, usernameKey);
					user = DB_User.createNew(user_email, username, confirmation.getString("confirmation_pwd"), userKey);

					txn.put(user, usernameEnt);
					txn.commit();

					LOG.fine("Registered user with email: " + user_email + ", username:" + username);
					String htmlContent = "<meta http-equiv=\"refresh\" content=\"0;URL=https://voluntier-317915.ew.r.appspot.com/pages/registerconfirmed.html\" />";

					return Response.ok(htmlContent, MediaType.TEXT_HTML).build();
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

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegistration(RegisterData reg_data) {
		LOG.fine("Attempt to register by user: " + reg_data.email);

		if (!reg_data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(reg_data.email);
			Entity user = txn.get(userKey);
			
			if (user != null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Email already in use: " + reg_data.email).build();
			} else {
				Key usernameKey = usernamesFactory.newKey(reg_data.username);
				Entity usernameEnt = txn.get(usernameKey);
				if(usernameEnt != null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("Username already in use: " + reg_data.username).build();
				}

				Key emailKey = serviceEmailFactory.newKey("confirmation-email");
				Entity serviceEmail = txn.get(emailKey);

				if (serviceEmail != null) {
					try {
						ConfirmationData url = ConfirmRegistrationEmail
								.sendConfirmationEmail(serviceEmail.getString("email"), reg_data);

						Key confirmationKey = confirmationFactory.newKey(url.code);
						Entity confirmation = Entity.newBuilder(confirmationKey).set("confirmation_code", url.code)
								.set("confirmation_email", url.email).set("confirmation_pwd", url.password)
								.set("confirmation_username", url.username)
								.set("confirmation_creation_date", url.creationDate)
								.set("confirmation_expiration_date", url.expirationDate).build();

						txn.add(confirmation);
						txn.commit();

						LOG.fine("Confirmation url generated: " + url.code);
						return Response.status(Status.NO_CONTENT).build();
					} catch (MessagingException e) {
						txn.rollback();
						LOG.severe("Something went wrong while sending confirmation email." + e.getMessage());
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
