package pt.unl.fct.di.aldeia.apdc2021.data.Room;


import androidx.room.Entity;

import androidx.annotation.NonNull;


@Entity(primaryKeys = {"event_id", "route_id"})
public class EventRouteCrossReference {
    @NonNull
    public String event_id;
    @NonNull
    public String route_id;

    public EventRouteCrossReference(@NonNull String event_id, @NonNull String route_id) {

        this.event_id = event_id;
        this.route_id = route_id;
        }
    }

