package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class SearchEventsReply {

    private List<SearchEventsReplyUnit> events;
    private String region_hash;


    public SearchEventsReply(List<SearchEventsReplyUnit> events, String region_hash) {
        this.events = events;
        this.region_hash = region_hash;
    }

    public List<SearchEventsReplyUnit> getEvents() {
        return events;
    }

    public String getRegion_hash() {
        return region_hash;
    }
}
