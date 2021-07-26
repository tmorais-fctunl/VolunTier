package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RouteEventInfo {
    private String name;
    private String event_id;
    private Double[] location;
    private int num_participants;
    private String start_date;
    private String end_date;
    private String visibility;

    public RouteEventInfo(String name, String event_id, Double[] location, int num_participants, String start_date, String end_date, String visibility) {
        this.name = name;
        this.event_id = event_id;
        this.location = location;
        this.num_participants = num_participants;
        this.start_date = start_date;
        this.end_date = end_date;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public Double[] getLocation() {
        return location;
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

    public String getVisibility() {
        return visibility;
    }
}
