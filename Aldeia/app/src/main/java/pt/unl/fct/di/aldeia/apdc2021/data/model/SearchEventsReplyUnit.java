package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class SearchEventsReplyUnit {
    private String name;
    private String event_id;
    private double[] location;
    private int num_participants;
    private String start_date;
    private String end_date;


    public SearchEventsReplyUnit(String name, String event_id, double[] location, int num_participants, String start_date, String end_date) {
        this.name = name;
        this.event_id = event_id;
        this.location=location;
        this.num_participants = num_participants;
        this.start_date = start_date;
        this.end_date = end_date;
    }
    public String getName() {
        return name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public double[] getLocation(){
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
}

