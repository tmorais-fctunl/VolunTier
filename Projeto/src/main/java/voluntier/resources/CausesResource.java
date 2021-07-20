package voluntier.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Transaction;

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.consumes.causes.CreateCauseData;
import voluntier.util.userdata.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CausesResource {
	private static final Logger LOG = Logger.getLogger(CausesResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public CausesResource() {
	}

	@POST
	@Path("/causes/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCause(CreateCauseData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			if(!ActionsResource.hasCausePermission(DB_User.getUser(data.email))) {
				txn.rollback();
				LOG.severe("User: " + data.email + " tried to create a new cause without permission");
				return Response.status(Status.FORBIDDEN).build();	
			}
			
			txn.commit();

			return Response.ok().build();

		} catch (InvalidTokenException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

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
