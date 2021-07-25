package pt.unl.fct.di.aldeia.apdc2021.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer emailError;
    @Nullable
    private Integer password2Error;
    private final boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer password2Error) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.emailError=emailError;
        this.password2Error=password2Error;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getPassword2Error() {
        return password2Error;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    boolean isDataValid() {
        return isDataValid;
    }
}
