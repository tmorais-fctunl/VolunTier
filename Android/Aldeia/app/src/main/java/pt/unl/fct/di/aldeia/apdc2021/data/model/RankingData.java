package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RankingData {

    String username;
    String full_name;
    String email;
    int score;
    String pic_64;

    public RankingData(String username, String full_name, String email, int score, String pic_64) {
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.score = score;
        this.pic_64 = pic_64;
    }

    public String getUsername() {
        return username;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return score;
    }

    public String getPic_64() {
        return pic_64;
    }
}