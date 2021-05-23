package pt.unl.fct.di.aldeia.apdc2021.ui.login;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private UserAuthenticated success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable UserAuthenticated success) {
        this.success = success;
    }

    @Nullable
    UserAuthenticated getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}