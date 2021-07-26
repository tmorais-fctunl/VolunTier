package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class SearchData {

    String username;
    String full_name;
    String email;
    String profile;
    String pic_64;

    public SearchData(String username, String full_name, String email, String profile, String pic_64) {
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.profile = profile;
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

    public String getProfile() {
        return profile;
    }

    public String getPic_64() {
        return pic_64;
    }
}