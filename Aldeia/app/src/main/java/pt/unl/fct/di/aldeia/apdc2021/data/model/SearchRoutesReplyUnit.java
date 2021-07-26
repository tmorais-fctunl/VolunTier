package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class SearchRoutesReplyUnit {
    private String route_name;
    private String description;
    private String route_id;
    private String creation_date;
    private String creator; // email
    private double avg_rating ;// average rating (double)
    private double my_rating; // double
    private int num_participants; // int
    private String status;
    private List<SearchEventsReplyUnit> events;

    public SearchRoutesReplyUnit(String route_name, String description, String route_id, String creation_date, String creator, double avg_rating, double my_rating, int num_participants, String status, List<SearchEventsReplyUnit> events) {
        this.route_name = route_name;
        this.description = description;
        this.route_id = route_id;
        this.creation_date = creation_date;
        this.creator = creator;
        this.avg_rating = avg_rating;
        this.my_rating = my_rating;
        this.num_participants = num_participants;
        this.status = status;
        this.events = events;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getDescription() {
        return description;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getCreator() {
        return creator;
    }

    public double getAvg_rating() {
        return avg_rating;
    }

    public double getMy_rating() {
        return my_rating;
    }

    public int getNum_participants() {
        return num_participants;
    }

    public String getStatus() {
        return status;
    }

    public List<SearchEventsReplyUnit> getEvents() {
        return events;
    }
}
