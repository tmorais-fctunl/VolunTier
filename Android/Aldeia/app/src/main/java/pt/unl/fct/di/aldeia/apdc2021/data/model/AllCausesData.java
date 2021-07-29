package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class AllCausesData {

    List<CausesData> causes;

    public AllCausesData(List<CausesData> causes) {
        this.causes = causes;
    }

    public List<CausesData> getCauses() {
        return causes;
    }
}