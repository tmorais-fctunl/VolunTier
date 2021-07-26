package pt.unl.fct.di.aldeia.apdc2021.ui.changePassword;

import androidx.annotation.Nullable;

public class ChangePasswordFormState {
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordConfirmationError;
    private boolean isDataValid;

    public ChangePasswordFormState(boolean isDataValid){
        this.isDataValid=true;
    }

    public ChangePasswordFormState(Integer passwordError,Integer passwordConfirmationError){
        isDataValid=false;
        this.passwordError=passwordError;
        this.passwordConfirmationError=passwordConfirmationError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getPasswordConfirmationError() {
        return passwordConfirmationError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
