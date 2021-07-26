package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoReply;

public class UpdatePhotoResult {
    @Nullable
    private final UpdatePhotoReply success;
    @Nullable
    private final Integer error;

    UpdatePhotoResult(@Nullable UpdatePhotoReply success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }

    @Nullable
    UpdatePhotoReply getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
