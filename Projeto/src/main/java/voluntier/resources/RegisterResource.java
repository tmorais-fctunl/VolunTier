package voluntier.resources;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;

import voluntier.util.Account;
import voluntier.util.RegisterData;
import voluntier.util.UserData;
import voluntier.util.UserDataFull;
import voluntier.util.email.ConfirmationEmail;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {
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
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(reg_data.user_id);
			Entity user = txn.get(userKey);

			if (user != null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User already exist: " + reg_data.user_id).build();
			} else {

				Key emailKey = datastore.newKeyFactory().setKind("ServiceEmail").newKey("confirmation-email");
				Entity serviceEmail = txn.get(emailKey);

				if (serviceEmail != null) {
					try {
						ConfirmationEmail.sendConfirmationEmail(serviceEmail.getString("email"), reg_data.email);
					} catch (MessagingException e) {
						txn.rollback();
						LOG.severe("Something went wrong while sending confirmation email.");
						return Response.status(Status.BAD_GATEWAY).build();
					}
					UserDataFull data = new UserDataFull(reg_data);
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
					return Response.ok(data.user_id + " registered").build();
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
