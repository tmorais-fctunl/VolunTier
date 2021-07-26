package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.annotation.Nullable;

public class RecoverPwFormState {

    @Nullable
    private final Integer emailError;
    private final boolean isDataValid;

    RecoverPwFormState(@Nullable Integer emailError) {
        this.emailError = emailError;
        this.isDataValid = false;
    }

    RecoverPwFormState(boolean isDataValid) {
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
