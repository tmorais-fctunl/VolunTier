package pt.unl.fct.di.aldeia.apdc2021.ui.changePassword;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class ChangePasswordResult {

        @Nullable
        private Integer success;
        @Nullable
        private Integer error;

        ChangePasswordResult(@Nullable Integer success ,@Nullable Integer error) {
            this.success=success;
            this.error = error;
        }



        @Nullable
        Integer getSuccess() {
            return success;
        }

        @Nullable
        Integer getError() {
            return error;
        }
}

