package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class RouteWithEvents {
        @Embedded
        public RouteEntity route;
        @Relation(
                parentColumn = "route_id",
                entityColumn = "event_id",
                associateBy = @Junction(EventRouteCrossReference.class)
        )
        public List<EventEntity> eventsOnRoute;
    }

