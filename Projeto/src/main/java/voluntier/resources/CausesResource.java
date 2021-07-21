package voluntier.resources;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Pair;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;
import voluntier.exceptions.InexistentCauseException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.NotEnoughCurrencyException;
import voluntier.util.JsonUtil;
import voluntier.util.causesdata.DB_Cause;
import voluntier.util.consumes.RequestData;
import voluntier.util.consumes.causes.CauseData;
import voluntier.util.consumes.causes.CheckDonationUpdatesData;
import voluntier.util.consumes.causes.CreateCauseData;
import voluntier.util.consumes.causes.DonationDataConsume;
import voluntier.util.consumes.causes.DonatorsDataConsumes;
import voluntier.util.produces.AllCausesDataReturn;
import voluntier.util.produces.CauseIDReturn;
import voluntier.util.produces.DonatorsDataReturn;
import voluntier.util.produces.DownloadPictureReturn;
import voluntier.util.produces.PicturesReturn;
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
			
			Pair<Entity, String> cause = DB_Cause.createNew(data);
			
			txn.put(cause.getValue0());
			txn.commit();

			return Response.ok().entity(JsonUtil.json.toJson(new CauseIDReturn(cause.getValue1()))).build();

		} catch (InvalidTokenException | InexistentUserException e) {
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
	
	@POST
	@Path("/causes/donators")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDonators(DonatorsDataConsumes data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			DonatorsDataReturn donators_page = DB_Cause.getDonators(data.cause_id, data.cursor == null ? 0 : data.cursor);
			
			return Response.ok(JsonUtil.json.toJson(donators_page)).build();

		} catch (InvalidTokenException | InexistentCauseException | InexistentUserException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/causes/pictures/all")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPicturesFromCause(CauseData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			List<DownloadPictureReturn> download_urls = DB_Cause.getImagesDownloadURLs(data.cause_id);

			return Response.ok(JsonUtil.json.toJson(new PicturesReturn(download_urls))).build();

		} catch (InvalidTokenException | InexistentCauseException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/causes/checkupdates")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkUpdates(CheckDonationUpdatesData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			boolean has_updates = DB_Cause.checkUpdates(data.cause_id, data.time_millis);
			
			return Response.status(has_updates ? Status.FOUND : Status.NOT_MODIFIED).entity(JsonUtil.json.toJson(System.currentTimeMillis())).build();

		} catch (InvalidTokenException | InexistentCauseException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/causes/donate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response donate(DonationDataConsume data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();
		
		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			List<Entity> ents = DB_Cause.donate(data.cause_id, data.email, data.amount);
			ents.forEach(e -> txn.put(e));
			txn.commit();
			
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentCauseException | InexistentUserException | NotEnoughCurrencyException e) {
			txn.rollback();
			LOG.warning(e.getMessage());
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
	
	@POST
	@Path("/causes/get/all")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllCauses(RequestData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			AllCausesDataReturn all_causes = DB_Cause.getCauses();
			
			return Response.ok().entity(JsonUtil.json.toJson(all_causes)).build();

		} catch (InvalidTokenException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
