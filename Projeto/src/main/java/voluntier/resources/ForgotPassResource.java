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
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import voluntier.util.ChangePassData;
import voluntier.util.ForgotPassData;
import voluntier.util.RegisterData;
import voluntier.util.RequestData;
import voluntier.util.email.ConfirmationData;
import voluntier.util.email.ConfirmationEmail;
import voluntier.util.email.ForgotConfirmationEmail;
import voluntier.util.email.ForgotData;
import voluntier.util.userdata.Account;
import voluntier.util.userdata.UserData_AllProperties;
import voluntier.util.userdata.UserData_Modifiable;

@Path("/forgotPass")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ForgotPassResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory serviceEmailFactory = datastore.newKeyFactory().setKind("ServiceEmail");
	private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");

	public ForgotPassResource () {
	}

	@POST
	@Path("/{code}/change")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType)
	public Response changePass (@PathParam("code") String code, ChangePassData data) {
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
				
				if (user == null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User does not exist: " + user_id).build();
				} else {
					
					if (!data.isValid()) {
						txn.rollback();
						return Response.status(Status.BAD_REQUEST).entity("Passwords don't match").build();
					}
					
					user = Entity.newBuilder(userKey)
							.set("user_id", user.getString("user_id"))
							.set("user_pwd", UserData_Modifiable.hashPassword(data.password))
							.set("user_email", user.getString("user_email"))
							.set("user_role", user.getString("user_role"))
							.set("user_state", user.getString("user_state"))
							.set("user_profile", user.getString("user_profile"))
							.set("user_landline", user.getString("user_landline"))
							.set("user_mobile", user.getString("user_mobile"))
							.set("user_address", user.getString("user_address"))
							.set("user_address2", user.getString("user_address2"))
							.set("user_region", user.getString("user_region"))
							.set("user_pc", user.getString("user_pc"))
							.set("user_account", user.getString("user_account"))
							.build();	
					
					SessionResource.invalidateAllSessionsOfUser(user_id, txn);
					
					confirmation = Entity.newBuilder(confirmationKey).set("confirmation_code", confirmation.getString("confirmation_code"))
							.set("user_id", confirmation.getString("user_id"))
							.set("confirmation_email", confirmation.getString("confirmation_email"))
							.set("confirmation_creation_date", confirmation.getLong("confirmation_expiration_date"))
							.set("confirmation_expiration_date", System.currentTimeMillis())
							.build();
					
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
	@Path("/{code}/confirm")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response doConfirmation(@PathParam("code") String code ) {
		Transaction txn = datastore.newTransaction();

		try {
			Key confirmationKey = confirmationFactory.newKey(code);
			Entity confirmation = txn.get(confirmationKey);

			if (confirmation == null || confirmation.getLong("confirmation_expiration_date") < System.currentTimeMillis()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Invalid confirmation url").build();
			} else {
				String htmlContent = "<html><head><style>iframe[seamless]{border: none;}</style></head>"
						+ "<body><iframe src=\"https://voluntier-312115.ew.r.appspot.com/www/pages/pass/change_pass.html\" "
						+ "seamless=\"\"></iframe></body></html>";

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

			if (user == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User does not exist: " + data.user_id).build();
			} else {
				
				if (!user.getString("user_email").equals(data.email)) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("User email does not match the email given.").build();
				}
				
				Key emailKey = serviceEmailFactory.newKey("confirmation-email");
				Entity serviceEmail = txn.get(emailKey);

				if (serviceEmail != null) {
					try {
						ForgotData url = ForgotConfirmationEmail.sendConfirmationEmail(serviceEmail.getString("email"), data);

						Key confirmationKey = confirmationFactory.newKey(url.code);
						Entity confirmation = Entity.newBuilder(confirmationKey).set("confirmation_code", url.code)
								.set("user_id", url.user_id).set("confirmation_email", url.email)/*.set("pwd", url.password)*/
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
