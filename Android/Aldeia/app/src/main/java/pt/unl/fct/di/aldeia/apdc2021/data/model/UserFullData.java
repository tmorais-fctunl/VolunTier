package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.Set;

public class UserFullData {
    private String username;
    private String email;
    private String full_name;
    private String profile;
    private String landline;
    private String mobile;
    private String address;
    private String address2;
    private String region;
    private String pc;
    private String website;
    private String facebook;
    private String instagram;
    private String twitter;
    private String pic_64;
    private Set<String> events_participating;
    private Set<String> events_created;
    private float currentCurrency;

    public UserFullData(String username, String email, String full_name, String profile, String landline,
                        String mobile, String address, String address2, String region, String pc, String website,
                        String facebook, String instagram, String twitter, String pic_64,
                        Set<String> events_participating, Set<String> events_created, float currentCurrency) {
        this.username = username;
        this.email = email;
        this.full_name = full_name;
        this.profile = profile;
        this.landline = landline;
        this.mobile = mobile;
        this.address = address;
        this.address2 = address2;
        this.region = region;
        this.pc = pc;
        this.website = website;
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitter = twitter;
        this.pic_64=pic_64;
        this.events_participating = events_participating;
        this.events_created = events_created;
        this.currentCurrency=currentCurrency;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return full_name;
    }

    public String getProfile() {
        return profile;
    }

    public String getLandline() {
        return landline;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public String getRegion() {
        return region;
    }

    public String getPc() {
        return pc;
    }

    public String getWebsite() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getPic() {
        return pic_64;
    }

    public Set<String> getEvents_participating() {
        return events_participating;
    }

    public Set<String> getEvents_created() {
        return events_created;
    }

    public float getCurrentCurrency() {
        return currentCurrency;
    }

    public void setCurrentCurrency(float currentCurrency){
        this.currentCurrency=currentCurrency;
    }
}