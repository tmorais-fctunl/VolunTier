package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class EventFullData {

    private String profile;
    private String name;
    private String event_id;
    private double[] location;
    private String start_date;
    private String end_date;
    private String creation_date;
    private String owner_email;
    private String contact;
    private int num_participants;
    private String description;
    private String category;
    private int capacity;
    private String website;
    private String facebook;
    private String instagram;
    private String twitter;

    public EventFullData(String profile, String name, String event_id, double[] location, String start_date, String end_date, String creation_date, String owner_email, String contact, int num_participants, String description, String category, int capacity, String website, String facebook, String instagram, String twitter) {
        this.profile = profile;
        this.name = name;
        this.event_id = event_id;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.creation_date = creation_date;
        this.owner_email = owner_email;
        this.contact = contact;
        this.num_participants = num_participants;
        this.description = description;
        this.category = category;
        this.capacity = capacity;
        this.website = website;
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitter = twitter;
    }

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getEventId() {
        return event_id;
    }

    public double[] getLocation() {
        return location;
    }

    public String getStartDate() {
        return start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public String getCreationDate() {
        return creation_date;
    }

    public String getOwnerEmail() {
        return owner_email;
    }

    public String getContact() {
        return contact;
    }

    public int getNumParticipants() {
        return num_participants;
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
}
