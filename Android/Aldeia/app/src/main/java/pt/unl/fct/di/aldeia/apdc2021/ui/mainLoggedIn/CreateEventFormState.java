package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

public class CreateEventFormState {

    @Nullable
    private final Integer nameError;
    @Nullable
    private final Integer dateError;
    @Nullable
    private final Integer descriptionError;
    @Nullable
    private final Integer contactError;

    private final boolean isDataValid;

    CreateEventFormState(@Nullable Integer nameError, @Nullable Integer dateError,@Nullable Integer descriptionError, @Nullable Integer contactError) {
        this.contactError = contactError;
        this.dateError=dateError;
        this.nameError=nameError;
        this.descriptionError=descriptionError;
        this.isDataValid = false;
    }

    CreateEventFormState(boolean isDataValid) {
        this.contactError = null;
        this.dateError = null;
        this.descriptionError = null;
        this.nameError=null;
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
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}