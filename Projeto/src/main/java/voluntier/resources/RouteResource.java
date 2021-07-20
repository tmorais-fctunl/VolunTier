package voluntier.resources;

import java.net.URL;
import java.util.LinkedList;
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
import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.CannotParticipateInSomeEventsException;
import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.RouteAlreadyExistsException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.ParticipantsData;
import voluntier.util.consumes.event.TargetData;
import voluntier.util.consumes.route.CreateRouteData;
import voluntier.util.consumes.route.DeleteRoutePictureData;
import voluntier.util.consumes.route.RateData;
import voluntier.util.consumes.route.RouteData;
import voluntier.util.eventdata.ParticipantDataReturn;
import voluntier.util.produces.CreateRouteReturn;
import voluntier.util.produces.DownloadPictureReturn;
import voluntier.util.produces.ParticipantsReturn;
import voluntier.util.produces.PicturesReturn;
import voluntier.util.produces.UploadPictureReturn;
import voluntier.util.produces.UserRoutesReturn;
import voluntier.util.routedata.DB_Route;
import voluntier.util.routedata.RouteDataReturn;
import voluntier.util.userdata.DB_User;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RouteResource {
	private static final Logger LOG = Logger.getLogger(RouteResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RouteResource() {
	}

	@POST
	@Path("/route/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRoute(CreateRouteData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Pair<List<Entity>, String> created_data = DB_Route.createNew(data);
			List<Entity> ents = created_data.getValue0();
			String route_id = created_data.getValue1();

			ents.forEach(e -> txn.put(e));

			txn.commit();

			return Response.ok(JsonUtil.json.toJson(new CreateRouteReturn(route_id))).build();

		} catch (InvalidTokenException | IllegalCoordinatesException | RouteAlreadyExistsException
				| InexistentEventException | ImpossibleActionException | InexistentUserException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/route/participate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response participate(RouteData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			List<Entity> ents = DB_Route.participate(data.route_id, data.email);

			ents.forEach(e -> txn.put(e));

			txn.commit();

			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentRouteException | InexistentEventException | AlreadyExistsException
				| ImpossibleActionException | InexistentUserException | CannotParticipateInSomeEventsException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/route/participants")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getParticipants(ParticipantsData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<List<ParticipantDataReturn>, Integer, MoreResultsType> return_data = DB_Route
					.getParticipants(data.route_id, data.cursor == null ? 0 : data.cursor, data.email);

			List<ParticipantDataReturn> participants = return_data.getValue0();
			Integer cursor = return_data.getValue1();
			MoreResultsType result = return_data.getValue2();

			return Response.ok(JsonUtil.json.toJson(new ParticipantsReturn(participants, cursor, result))).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentUserException | ImpossibleActionException
				| InexistentRouteException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/route/data")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRoute(RouteData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			RouteDataReturn route = DB_Route.getRouteData(data.route_id, data.email);

			return Response.ok(JsonUtil.json.toJson(route)).build();

		} catch (InvalidTokenException | InexistentRouteException | InexistentRatingException
				| InexistentChatIdException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/user/routes/created")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserRoutes(TargetData data) {
		LOG.fine("Trying to get user routes: " + data.target);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			Entity user = DB_User.getUser(data.target);

			List<String> ids = DB_User.getRouteIds(user);
			List<RouteDataReturn> routes = new LinkedList<>();
			ids.forEach(id -> {
				try {
					routes.add(new RouteDataReturn(DB_Route.getRoute(id), data.email));
				} catch (InexistentRouteException e) {}});
			
			return Response.ok(JsonUtil.json.toJson(new UserRoutesReturn(routes))).build();

		} catch (InvalidTokenException | InexistentUserException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/user/routes/participating")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserParticipatingRoutes(TargetData data) {
		LOG.fine("Trying to get user participating routes: " + data.target);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			Entity user = DB_User.getUser(data.target);

			List<String> ids = DB_User.getParticipatingRouteIds(user);
			List<RouteDataReturn> routes = new LinkedList<>();
			ids.forEach(id -> {
				try {
					routes.add(new RouteDataReturn(DB_Route.getRoute(id), data.email));
				} catch (InexistentRouteException e) {}});
			
			return Response.ok(JsonUtil.json.toJson(new UserRoutesReturn(routes))).build();

		} catch (InvalidTokenException | InexistentUserException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/route/pictures/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPictureToRoute(RouteData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Pair<Entity, String> res = DB_Route.addPicture(data.route_id, data.email);
			Entity route = res.getValue0();
			String filename = res.getValue1();

			URL upload_url = GoogleStorageUtil.signURLForUpload(filename);

			txn.put(route);
			txn.commit();

			return Response.ok(JsonUtil.json.toJson(new UploadPictureReturn(upload_url, filename))).build();

		} catch (InvalidTokenException | ImpossibleActionException | MaximumSizeReachedException
				| InexistentRouteException | SomethingWrongException | InexistentParticipantException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/route/pictures/delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePictureFromRoute(DeleteRoutePictureData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity updated_route = DB_Route.deletePicture(data.route_id, data.pic_id, data.email);

			txn.put(updated_route);
			txn.commit();

			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentRouteException
				| InexistentParticipantException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/route/pictures/all")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPicturesFromRoute(RouteData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			List<DownloadPictureReturn> download_urls = DB_Route.getPicturesDownloadURLs(data.route_id);

			return Response.ok(JsonUtil.json.toJson(new PicturesReturn(download_urls))).build();

		} catch (InvalidTokenException | InexistentRouteException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/route/rate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rateRoute(RateData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity rating = DB_Route.giveRating(data.route_id, data.email, data.rating);

			txn.put(rating);
			txn.commit();

			LOG.fine("Route rated correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentRouteException | InexistentParticipantException
				| InexistentRatingException e) {
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
