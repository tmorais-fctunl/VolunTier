package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteParticipantsData;

public class GetRouteParticipantsRepository{
    private static volatile GetRouteParticipantsRepository instance;

    private final GetRouteParticipantsDataSource dataSource;

    private GetRouteParticipantsRepository(GetRouteParticipantsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static GetRouteParticipantsRepository getInstance(GetRouteParticipantsDataSource dataSource) {
        if (instance == null) {
            instance = new GetRouteParticipantsRepository(dataSource);
        }
        return instance;
    }
    public Result<GetParticipantsReply> getParticipants(GetRouteParticipantsData event) {
        return dataSource.getParticipants(event);
    }

}