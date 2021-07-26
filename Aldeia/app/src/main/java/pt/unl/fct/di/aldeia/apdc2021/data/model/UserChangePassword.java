package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserChangePassword {

    private String email;
    private String token;
    private String target;



    private String old_password;
    private String password;
    private String confirmation_password;

    public UserChangePassword(String email, String token, String target, String old_password, String password, String confirmation_password){
        this.email=email;
        this.token=token;
        this.target = target;
        this.old_password=old_password;
        this.password=password;
        this.confirmation_password=confirmation_password;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getTarget() {
        return target;
    }

    public String getOld_password() {
        return old_password;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmation_password() {
        return confirmation_password;
    }
}
