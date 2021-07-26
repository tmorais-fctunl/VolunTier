package pt.unl.fct.di.aldeia.apdc2021.data.model;

import com.google.android.gms.maps.model.LatLng;

public class EventMarkerData {
    private String event_id;
    private double[] coordinates;

    public EventMarkerData (String event_id, double[] coordinates) {
        this.event_id = event_id;
        this.coordinates = coordinates;
    }

    public String getEvent_id() {
        return event_id;
    }

    public double[] getCoordinates() {
        return coordinates;
    }
}
