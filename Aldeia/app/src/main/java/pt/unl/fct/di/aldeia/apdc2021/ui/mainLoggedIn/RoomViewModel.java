package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventRouteCrossReference;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventsRoomRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.RouteEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.RouteWithEvents;

public class RoomViewModel extends AndroidViewModel {
    private EventsRoomRepository repository;
    private LiveData<List<EventEntity>> queryResult=new MutableLiveData<>();
    private List<String> hashesFullyLoaded;
    private List<String> hashesLoadedInStorage;
    private MutableLiveData<String> GeoHash = new MutableLiveData<String>();
    private MutableLiveData<String> GeoHashRoutes = new MutableLiveData<>();
    private LiveData<List<EventEntity>> events = Transformations.switchMap(GeoHash, (geoHash -> repository.getEventsByHashLoc(geoHash)));
    private LiveData<List<RouteEntity>> routes = Transformations.switchMap(GeoHashRoutes, (geoHashRoutes -> repository.getRoutesByHashLoc(geoHashRoutes)));

    public RoomViewModel(Application application){
        super(application);
        repository= new EventsRoomRepository(application);
        hashesFullyLoaded=new ArrayList<>();
    }

    public void setCurrentGeoHash(String geoHash){
        GeoHash.postValue(geoHash);
    }

    public void loadRoutes(){
        GeoHashRoutes.postValue(GeoHash.getValue());
    }

    public List<String> getHashesFullyLoaded(){return hashesFullyLoaded;}

    public void addHashFullyLoaded(String hash){hashesFullyLoaded.add(hash);}

    public void removeHashFullyLoaded(String hash){hashesFullyLoaded.remove(hash);}

    public List<String> getHashesLoadedInStorage(){return hashesLoadedInStorage;}

    public void addHashLoadedInStorage(String hash){hashesLoadedInStorage.add(hash);}

    public void insertMultipleEvents(List<EventEntity> events) { repository.insertMultipleEvents(events); }

    public void insertEvent(EventEntity event){
        repository.insertEvent(event);
    }

    public LiveData<List<EventEntity>> getEventQueryResult(){
        return events;
    }

    public LiveData<List<RouteEntity>> getRoutesQueryResult(){
        return routes;
    }

    public void updateEvent(EventEntity event){
        repository.updateEvent(event);
    }

    public void deleteEvent(String id){
        repository.deleteEvent(new EventEntity(id,0,0,null,0,null,null,null));
    }

    public void insertMultipleRoutes(List<RouteEntity> routes) { repository.insertMultipleRoutes(routes); }

    public void insertRoute(RouteEntity route){
        repository.insertRoute(route);
    }

    public void updateRoute(RouteEntity route){
        repository.updateRoute(route);
    }

    public void deleteRoute(String id){
        repository.deleteRoute(new RouteEntity(id,null));
    }

    public void insertCrossovers(List<EventRouteCrossReference> crossReferences){
        repository.insertMultipleCrossovers(crossReferences);
    }

    public LiveData<List<RouteWithEvents>> getRoutesWithEvents(){return repository.getRouteWithEvents();}

    public void clear(){
        repository.clear();
    }

    public void deleteCrossOverFromRouteID(String route_id){
        repository.deleteCrossOversFromRouteID(route_id);
    }

    public void deleteCrossOverFromEventD(String event_id){
        repository.deleteCrossOversFromEventID(event_id);
    }
}
