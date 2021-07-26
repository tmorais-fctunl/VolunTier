package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class SearchUserData {

    String results;
    List<SearchData> users;
    String[] cursor;


    public SearchUserData(String results, List<SearchData> users, String[] cursor) {
        this.results = results;
        this.users = users;
        this.cursor = cursor;
    }

    public String getResults() {
        return results;
    }

    public List<SearchData> getUsers() {
        return users;
    }

    public String[] getCursor() {
        return cursor;
    }
}