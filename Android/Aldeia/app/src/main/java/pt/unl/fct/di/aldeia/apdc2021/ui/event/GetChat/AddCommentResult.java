package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;

public class AddCommentResult {
    @Nullable
    private final AddCommentReply success;
    @Nullable
    private final Integer error;

    public AddCommentResult(@Nullable AddCommentReply success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public AddCommentReply getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
