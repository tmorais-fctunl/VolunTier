package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class SearchRoutesReply {
    private List<SearchRoutesReplyUnit> routes;
    private String region_hash;

    public SearchRoutesReply(List<SearchRoutesReplyUnit> routes, String region_hash) {
        this.routes = routes;
        this.region_hash = region_hash;
    }

    public List<SearchRoutesReplyUnit> getRoutes() {
        return routes;
    }

    public String getRegion_hash() {
        return region_hash;
    }
}
