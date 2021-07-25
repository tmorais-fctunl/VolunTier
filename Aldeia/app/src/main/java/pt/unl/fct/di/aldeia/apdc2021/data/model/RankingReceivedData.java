package pt.unl.fct.di.aldeia.apdc2021.data.model;

import android.service.notification.NotificationListenerService;

import java.util.List;

public class RankingReceivedData {

    String results;
    List<RankingData> users;
    String cursor;
    YourData current_user;


    public RankingReceivedData(String results, List<RankingData> ranking, String cursor, YourData current_user) {
        this.results = results;
        this.users = ranking;
        this.cursor = cursor;
        this.current_user=current_user;
    }

    public String getResults() {
        return results;
    }

    public List<RankingData> getRanking() {
        return users;
    }

    public String getCursor() {
        return cursor;
    }

    public YourData getCurrent_user() {
        return current_user;
    }

}