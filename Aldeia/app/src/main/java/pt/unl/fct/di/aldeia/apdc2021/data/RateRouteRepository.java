package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteRatingData;

public class RateRouteRepository {
    private static volatile RateRouteRepository instance;

    private final RateRouteDataSource dataSource;

    // private constructor : singleton access
    private RateRouteRepository(RateRouteDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RateRouteRepository getInstance(RateRouteDataSource dataSource) {
        if (instance == null) {
            instance = new RateRouteRepository(dataSource);
        }
        return instance;
    }
    public Result<Void> rateRoute(String email, String token, String route_id, float rating) {
        return dataSource.rateRoute(new RouteRatingData(email, token, route_id, rating));
    }

}