package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.annotation.Nullable;

public class RecoverPwFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer emailError;
    private boolean isDataValid;

    RecoverPwFormState(@Nullable Integer usernameError, @Nullable Integer emailError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.isDataValid = false;
    }

    RecoverPwFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
