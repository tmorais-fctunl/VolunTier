package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class RouteEntity {
    @PrimaryKey
    @NonNull
    private String route_id;


    @ColumnInfo(name = "hashed_location")
    private String hashed_location;

    public RouteEntity(@NonNull String route_id, String hashed_location) {
        this.route_id = route_id;
        this.hashed_location = hashed_location;
    }

    @NonNull
    public String getRoute_id() {
        return route_id;
    }

    public String getHashed_location() {
        return hashed_location;
    }
}
