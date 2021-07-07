package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.Nullable;

public class UpdateEventFormState {

    @Nullable
    private final Integer contactError;
    @Nullable
    private final Integer startDateError;
    @Nullable
    private final Integer endDateError;
    @Nullable
    private final Integer categoryError;
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

    UpdateEventFormState(@Nullable Integer contactError, @Nullable Integer startDateError, @Nullable Integer endDateError, @Nullable Integer categoryError, @Nullable Integer websiteError,
                         @Nullable Integer facebookError,@Nullable Integer instagramError,@Nullable Integer twitterError , @Nullable Integer descriptionError) {
        this.contactError = contactError;
        this.startDateError=startDateError;
        this.endDateError=endDateError;
        this.categoryError=categoryError;
        this.websiteError=websiteError;
        this.descriptionError=descriptionError;
        this.facebookError=facebookError;
        this.instagramError=instagramError;
        this.twitterError=twitterError;
        this.isDataValid = false;
    }

    UpdateEventFormState(boolean isDataValid) {
        this.contactError = null;
        this.startDateError = null;
        this.endDateError=null;
        this.categoryError = null;
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
    public Integer getStartDateError() {
        return startDateError;
    }

    @Nullable
    public Integer getEndDateError() {
        return endDateError;
    }

    @Nullable
    public Integer getCategoryError() {
        return categoryError;
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
