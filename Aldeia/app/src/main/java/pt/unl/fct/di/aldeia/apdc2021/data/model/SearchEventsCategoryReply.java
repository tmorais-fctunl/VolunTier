package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class SearchEventsCategoryReply {

    private List<SearchEventsReplyUnit> events;
    private String cursor;
    private String results;


    public SearchEventsCategoryReply(List<SearchEventsReplyUnit> events, String cursor, String results) {
        this.events = events;
        this.cursor = cursor;
        this.results = results;
    }

    public List<SearchEventsReplyUnit> getEvents() {
        return events;
    }

    public String getCursor() {
        return cursor;
    }

    public String getResults() {
        return results;
    }
}