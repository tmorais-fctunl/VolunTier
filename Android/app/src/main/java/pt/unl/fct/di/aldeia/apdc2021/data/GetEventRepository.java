package pt.unl.fct.di.aldeia.apdc2021.data;

import android.util.EventLog;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class GetEventRepository {

    private static volatile GetEventRepository instance;

    private final GetEventDataSource getEventDataSource;
    private final UpdateEventDataSource updateEventDataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private GetEventRepository(GetEventDataSource getEventDataSource, UpdateEventDataSource updateEventDataSource) {
        this.updateEventDataSource=updateEventDataSource;
        this.getEventDataSource = getEventDataSource;

    }

    public static GetEventRepository getInstance(GetEventDataSource getEventDataSource,UpdateEventDataSource updateEventDataSource) {
        if (instance == null) {
            instance = new GetEventRepository(getEventDataSource,updateEventDataSource);
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
}