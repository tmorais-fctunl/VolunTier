package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserFullData {
    private final String username;
    private final String email;
    private final String full_name;
    private final String profile;
    private final String landline;
    private final String mobile;
    private final String address;
    private final String address2;
    private final String region;
    private final String pc;
    private final String website;
    private final String facebook;
    private final String instagram;
    private final String twitter;

    public UserFullData(String username, String email, String full_name, String profile, String landline,
                        String mobile, String address, String address2, String region, String pc, String website, String facebook, String instagram, String twitter) {
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


}
