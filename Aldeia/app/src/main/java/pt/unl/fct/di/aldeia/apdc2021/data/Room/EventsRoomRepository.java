package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import okhttp3.Route;

public class EventsRoomRepository {

    private EventDao eventDao;

    public EventsRoomRepository(Application application) {
        EventsDatabase db = EventsDatabase.getDatabase(application);
        eventDao = db.eventDao();
    }

    public LiveData<List<EventEntity>> getEventsByHashLoc(String location) {
        return eventDao.loadAllEventsFromLoc(location);
    }

    public LiveData<List<RouteEntity>> getRoutesByHashLoc(String location) {
        return eventDao.loadAllRoutesFromLoc(location);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insertMultipleEvents(List<EventEntity> events) {
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertEvents(events);
        });
    }

    public void insertEvent(EventEntity event){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertEvent(event);
        });
    }

    public void updateEvent(EventEntity event){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.updateEvent(event);
        });
    }

    public void deleteEvent(EventEntity id){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.delete(id);
        });
    }

    public void clear( ){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.nukeTable();
        });
    }

    public void insertMultipleRoutes(List<RouteEntity> routes) {
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertRoutes(routes);
        });
    }

    public void insertRoute(RouteEntity route){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertRoute(route);
        });
    }

    public void updateRoute(RouteEntity route){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.updateRoute(route);
        });
    }

    public void deleteRoute(RouteEntity route){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.delete(route);
        });
    }

    public void insertMultipleCrossovers(List<EventRouteCrossReference> crossReferences) {
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertCrossover(crossReferences);
        });
    }
    public LiveData<List<RouteWithEvents>> getRouteWithEvents(){
        return eventDao.getRouteWithEvents();
    }

    public void deleteCrossOversFromEventID(String event_Id){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.deleteCrossoverFromEventID(event_Id);
        });
    }

    public void deleteCrossOversFromRouteID(String route_Id){
        EventsDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.deleteCrossoverFromRouteID(route_Id);
        });
    }
}
