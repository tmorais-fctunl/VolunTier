package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;

public class GetEventParticipantsRepository{
    private static volatile GetEventParticipantsRepository instance;

    private final GetEventParticipantsDataSource dataSource;

    // private constructor : singleton access
    private GetEventParticipantsRepository(GetEventParticipantsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static GetEventParticipantsRepository getInstance(GetEventParticipantsDataSource dataSource) {
        if (instance == null) {
            instance = new GetEventParticipantsRepository(dataSource);
        }
        return instance;
    }
    public Result<GetParticipantsReply> getParticipants(GetEventParticipantsData event) {
        return dataSource.getParticipants(event);
    }

}
