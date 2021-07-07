package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class CreateEventData {
    String token;
    String email;
    String event_name;
    double[] location;
    String start_date;
    String end_date;

    public CreateEventData(String email, String token, String eventName, double[] point, String startDate, String endDate) {
        this.email = email;
        this.token = token;
        this.event_name = eventName;
        this.location = point;
        this.start_date = startDate;
        this.end_date = endDate;
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
}

