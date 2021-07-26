package pt.unl.fct.di.aldeia.apdc2021.ui.shop;

import androidx.annotation.Nullable;

import java.io.Serializable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class AllCausesResult {
    @Nullable
    private final AllCausesData success;
    @Nullable
    private final Integer error;

    public AllCausesResult(@Nullable AllCausesData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public AllCausesData getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}