package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import retrofit2.http.DELETE;

@Dao
public interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<EventEntity> events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(EventEntity event);

    @Update
    void updateEvent(EventEntity event);

    @Delete
    void delete(EventEntity event);

    @Query("SELECT * FROM EventEntity WHERE hashed_location = :hashedLocation")
    LiveData<List<EventEntity>> loadAllEventsFromLoc(String hashedLocation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoutes(List<RouteEntity> routes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoute(RouteEntity route);

    @Update
    void updateRoute(RouteEntity route);

    @Delete
    void delete(RouteEntity route);

    @Query("SELECT * FROM RouteEntity WHERE hashed_location = :hashedLocation")
    LiveData<List<RouteEntity>> loadAllRoutesFromLoc(String hashedLocation);

    @Transaction
    @Query("SELECT * FROM RouteEntity")
    LiveData<List<RouteWithEvents>> getRouteWithEvents();

    @Query("DELETE FROM EventEntity")
    void nukeTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrossover(List<EventRouteCrossReference> crossover);

    @Query("DELETE FROM EVENTROUTECROSSREFERENCE WHERE route_id=:routeID ")
    void deleteCrossoverFromRouteID(String routeID);

    @Query("DELETE FROM EVENTROUTECROSSREFERENCE WHERE event_id=:eventID ")
    void deleteCrossoverFromEventID(String eventID);
}
