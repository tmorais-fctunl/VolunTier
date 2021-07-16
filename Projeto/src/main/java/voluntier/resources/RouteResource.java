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

import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.RouteAlreadyExistsException;
import voluntier.exceptions.InexistentEventException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.route.CreateRouteData;
import voluntier.util.produces.CreateRouteReturn;
import voluntier.util.routedata.DB_Route;

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

		} catch (InvalidTokenException | IllegalCoordinatesException | 
				RouteAlreadyExistsException | InexistentEventException | ImpossibleActionException e) {
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

}
