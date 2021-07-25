package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LeaveRouteData;

public class GetRouteRepository {
    private static volatile GetRouteRepository instance;

    private final GetRouteDataSource getRouteDataSource;
    private final DeleteRouteDataSource deleteRouteDataSource;
    private final ParticipateRouteDataSource participateRouteDataSource;
    private final LeaveRouteDataSource leaveRouteDataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private GetRouteRepository(GetRouteDataSource getRouteDataSource,DeleteRouteDataSource deleteRouteDataSource,ParticipateRouteDataSource participateRouteDataSource,LeaveRouteDataSource leaveRouteDataSource) {
        this.getRouteDataSource=getRouteDataSource;
        this.deleteRouteDataSource=deleteRouteDataSource;
        this.participateRouteDataSource=participateRouteDataSource;
        this.leaveRouteDataSource=leaveRouteDataSource;

    }

    public static GetRouteRepository getInstance(GetRouteDataSource getRouteDataSource,DeleteRouteDataSource deleteRouteDataSource,ParticipateRouteDataSource participateRouteDataSource,LeaveRouteDataSource leaveRouteDataSource) {
        if (instance == null) {
            instance = new GetRouteRepository(getRouteDataSource,deleteRouteDataSource,participateRouteDataSource,leaveRouteDataSource);
        }
        return instance;
    }


    public Result<GetRouteReply> getRoute(GetRouteData data) {
        Result<GetRouteReply> result = getRouteDataSource.getRoute(data);
        return result;
    }

    public Result<Void> deleteRoute(GetRouteData data) {
        Result<Void> result = deleteRouteDataSource.deleteRoute(data);
        return result;
    }

    public Result<Void> participateRoute(GetRouteData data) {
        Result<Void> result = participateRouteDataSource.participateRoute(data);
        return result;
    }

    public Result<Void> leaveRoute(LeaveRouteData data) {
        Result<Void> result = leaveRouteDataSource.leaveRoute(data);
        return result;
    }
}
