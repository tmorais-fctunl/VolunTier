package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class CreateEventData {
    String token;
    String email;
    String event_name;
    double[] location;
    String start_date;
    String end_date;
    String description;
    String category;
    String contact;
    int capacity;
    String profile;
    int difficulty;

    public CreateEventData(String email, String token, String eventName, double[] point, String startDate,
                           String endDate, String description, String category, String profile, String contact, int capacity,int difficulty) {
        this.email = email;
        this.token = token;
        this.event_name = eventName;
        this.location = point;
        this.start_date = startDate;
        this.end_date = endDate;
        this.description=description;
        this.category=category;
        this.contact = contact;
        this.capacity = capacity;
        this.profile = profile;
        this.difficulty=difficulty;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getEventName() {
        return event_name;
    }

    public double[] getPoint() {
        return location;
    }

    public String getStartDate() {
        return start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public String getContact () {
        return contact;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getProfile() {
        return profile;
    }

    public int getDifficulty() {
        return difficulty;
    }
}

