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

import voluntier.util.Account;
import voluntier.util.ConfirmationURL;
import voluntier.util.RegisterData;
import voluntier.util.UserData;
import voluntier.util.UserDataFull;
import voluntier.util.email.ConfirmationEmail;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory serviceEmailFactory = datastore.newKeyFactory().setKind("ServiceEmail");
	private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");

	public RegisterResource() {
	}

	@GET
	@Path("/{code}/confirm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doConfirmation(@PathParam("code") String code) {
		Transaction txn = datastore.newTransaction();
		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);
			
			if (confirmation == null || confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid confirmation url").build();
			} else {
				
				String user_id = confirmation.getString("user_id");

				Key userKey = usersFactory.newKey(user_id);
				Entity user = txn.get(userKey);
				if (user != null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User already exist: " + user_id).build();
				} else {
					
					UserDataFull data = new UserDataFull(new RegisterData(user_id, confirmation.getString("email"), confirmation.getString("pwd")));
					
					user = Entity.newBuilder(userKey).set("user_id", data.user_id).set("user_pwd", data.password)
							.set("user_email", data.email).set("user_role", data.getRole().toString())
							.set("user_state", data.getState().toString()).set("user_profile", data.profile)
							.set("user_landline", data.landline).set("user_mobile", data.mobile)
							.set("user_address", data.address).set("user_address2", data.address2)
							.set("user_region", data.region).set("user_pc", data.pc)
							.set("user_account", Account.ACTIVE.toString()).build();

					txn.add(user);
					txn.commit();

					LOG.fine("Registered user: " + data.user_id);
					return Response.ok("wowo you are foking registered, enjoy!").build();
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
		LOG.fine("Attempt to register by user: " + reg_data.user_id);

		if (!reg_data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(reg_data.user_id);
			Entity user = txn.get(userKey);

			if (user != null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User already exist: " + reg_data.user_id).build();
			} else {

				Key emailKey = serviceEmailFactory.newKey("confirmation-email");
				Entity serviceEmail = txn.get(emailKey);

				if (serviceEmail != null) {
					try {
						ConfirmationURL url = ConfirmationEmail.sendConfirmationEmail(serviceEmail.getString("email"), reg_data);

						Key confirmationKey = confirmationFactory.newKey(url.code);
						Entity confirmation = Entity.newBuilder(confirmationKey).set("confirmation_code", url.code)
								.set("user_id", url.user_id).set("email", url.email).set("pwd", url.password)
								.set("confirmation_creation_date", url.creationDate).set("confirmation_expiration_date", url.expirationDate)
								.build();

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
