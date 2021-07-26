package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class CausesData {

    String id;
    String name;
    int goal;
    int raised;
    String description;
    String company_name;
    String website;

    public CausesData(String id, String name, int goal, int raised, String description, String company_name, String website) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.raised = raised;
        this.description = description;
        this.company_name = company_name;
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGoal() {
        return goal;
    }

    public int getRaised() {
        return raised;
    }

    public String getDescription() {
        return description;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getWebsite() {
        return website;
    }
}