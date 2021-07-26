package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RemoveParticipantFromEventData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class GetEventRepository {

    private static volatile GetEventRepository instance;

    private final GetEventDataSource getEventDataSource;
    private final UpdateEventDataSource updateEventDataSource;
    private final RemoveEventDataSource removeEventDataSource;
    private final ParticipateEventDataSource participateEventDataSource;
    private final LeaveEventDataSource leaveEventDataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private GetEventRepository(GetEventDataSource getEventDataSource, UpdateEventDataSource updateEventDataSource, RemoveEventDataSource removeEventDataSource, ParticipateEventDataSource participateEventDataSource,LeaveEventDataSource leaveEventDataSource) {
        this.updateEventDataSource=updateEventDataSource;
        this.getEventDataSource = getEventDataSource;
        this.removeEventDataSource = removeEventDataSource;
        this.participateEventDataSource=participateEventDataSource;
        this.leaveEventDataSource=leaveEventDataSource;
    }

    public static GetEventRepository getInstance(GetEventDataSource getEventDataSource,UpdateEventDataSource updateEventDataSource, RemoveEventDataSource removeEventDataSource,ParticipateEventDataSource participateEventDataSource,LeaveEventDataSource leaveEventDataSource) {
        if (instance == null) {
            instance = new GetEventRepository(getEventDataSource,updateEventDataSource, removeEventDataSource,participateEventDataSource,leaveEventDataSource);
        }
        return instance;
    }


    public Result<EventFullData> getEvent(String email, String token, String event_id) {
        Result<EventFullData> result = getEventDataSource.getEvent(email, token, event_id);
        return result;
    }

    public Result<Void> updateEvent(EventUpdateData eventUpdateData){
        Result<Void> result= updateEventDataSource.updateEvent(eventUpdateData);
        return result;
    }

    public Result<Void> removeEvent(String email, String token, String event_id) {
        Result<Void> result = removeEventDataSource.removeEvent(email, token, event_id);
        return result;
    }

    public Result<Void> participateInEvent(GetEventData event) {
        return participateEventDataSource.participateEvent(event);
    }

    public Result<Void> leaveEvent(RemoveParticipantFromEventData event) {
        return leaveEventDataSource.leaveEvent(event);
    }
}