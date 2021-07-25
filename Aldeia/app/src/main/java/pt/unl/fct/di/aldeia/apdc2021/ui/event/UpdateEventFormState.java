package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.Nullable;

public class UpdateEventFormState {

    @Nullable
    private final Integer contactError;
    @Nullable
    private final Integer dateError;
    @Nullable
    private final Integer websiteError;
    @Nullable
    private final Integer facebookError;
    @Nullable
    private final Integer instagramError;
    @Nullable
    private final Integer twitterError;
    @Nullable
    private final Integer descriptionError;
    private final boolean isDataValid;

    UpdateEventFormState(@Nullable Integer contactError, @Nullable Integer dateError, @Nullable Integer websiteError,
                         @Nullable Integer facebookError,@Nullable Integer instagramError,@Nullable Integer twitterError , @Nullable Integer descriptionError) {
        this.contactError = contactError;
        this.dateError=dateError;
        this.websiteError=websiteError;
        this.descriptionError=descriptionError;
        this.facebookError=facebookError;
        this.instagramError=instagramError;
        this.twitterError=twitterError;
        this.isDataValid = false;
    }

    UpdateEventFormState(boolean isDataValid) {
        this.contactError = null;
        this.dateError = null;
        this.websiteError = null;
        this.descriptionError = null;
        this.twitterError=null;
        this.facebookError=null;
        this.instagramError=null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getContactError() {
        return contactError;
    }

    @Nullable
    public Integer getDateError() {
        return dateError;
    }

    @Nullable
    public Integer getWebsiteError() {
        return websiteError;
    }

    @Nullable
    public Integer getFacebookError() {
        return facebookError;
    }

    @Nullable
    public Integer getInstagramError() {
        return instagramError;
    }

    @Nullable
    public Integer getTwitterError() {
        return twitterError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
