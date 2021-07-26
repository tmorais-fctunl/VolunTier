package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventEntity {
    @PrimaryKey
    @NonNull
    private String event_id;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "event_name")
    private String name;

    @ColumnInfo(name = "participants")
    private int num_participants;

    @ColumnInfo(name = "start_date")
    private String start_date;

    @ColumnInfo(name = "end_date")
    private String end_date;


    @ColumnInfo(name = "hashed_location")
    private String hashed_location;

    public EventEntity(@NonNull String event_id, double latitude, double longitude, String name, int num_participants, String start_date, String end_date, String hashed_location) {
        this.event_id = event_id;
        this.latitude = latitude;
        this.longitude=longitude;
        this.name = name;
        this.num_participants = num_participants;
        this.start_date = start_date;
        this.end_date = end_date;
        this.hashed_location = hashed_location;
    }

    public String getEvent_id() {
        return event_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getNum_participants() {
        return num_participants;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getHashed_location() {
        return hashed_location;
    }
}
