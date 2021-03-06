package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class EventUpdateData {

    private String token;
    private String email;
    private String event_id;
    private int difficulty;
    private double[] location;
    private String event_name;
    private String start_date;
    private String end_date;
    private String contact;
    private String description;
    private String category;
    private int capacity;
    private String website;
    private String facebook;
    private String instagram;
    private String twitter;
    private String profile;

    public EventUpdateData(String email, String token, String event_id, double[] location, String event_name,String start_date, String end_date, String contact, String description, String category, int capacity, String website, String facebook, String instagram, String twitter,String profile, int difficulty) {
        this.email = email;
        this.token = token;
        this.event_id = event_id;
        this.location = location;
        this.event_name=event_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.contact = contact;
        this.description = description;
        this.category = category;
        this.capacity = capacity;
        this.website = website;
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitter = twitter;
        this.profile=profile;
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getEventId() {
        return event_id;
    }

    public double[] getLocation() {
        return location;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getStartDate() {
        return start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getCapacity() {
        return capacity;
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

    public String getProfile() {
        return profile;
    }
}