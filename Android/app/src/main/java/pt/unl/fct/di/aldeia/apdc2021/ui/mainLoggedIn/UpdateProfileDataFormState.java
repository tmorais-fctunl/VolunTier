package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

public class UpdateProfileDataFormState {

    @Nullable
    private final Integer postalCodeError;
    @Nullable
    private final Integer mobileError;
    @Nullable
    private final Integer landLineError;
    @Nullable
    private final Integer addressError;
    @Nullable
    private final Integer regionError;
    @Nullable
    private final Integer fullNameError;
    private final boolean isDataValid;

    UpdateProfileDataFormState(@Nullable Integer postalCodeError, @Nullable Integer mobileError, @Nullable Integer landLineError, @Nullable Integer addressError, @Nullable Integer regionError, @Nullable Integer fullNameError) {
        this.postalCodeError = postalCodeError;
        this.mobileError=mobileError;
        this.landLineError=landLineError;
        this.addressError=addressError;
        this.regionError=regionError;
        this.fullNameError=fullNameError;
        this.isDataValid = false;
    }

    UpdateProfileDataFormState(boolean isDataValid) {
        this.postalCodeError = null;
        this.mobileError = null;
        this.landLineError = null;
        this.addressError = null;
        this.regionError = null;
        this.fullNameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getPostalCodeError() {
        return postalCodeError;
    }

    @Nullable
    public Integer getMobileError() {
        return mobileError;
    }

    @Nullable
    public Integer getLandLineError() {
        return landLineError;
    }

    @Nullable
    public Integer getAddressError() {
        return addressError;
    }

    @Nullable
    public Integer getRegionError() {
        return regionError;
    }

    @Nullable
    public Integer getFullNameError() {
        return fullNameError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
