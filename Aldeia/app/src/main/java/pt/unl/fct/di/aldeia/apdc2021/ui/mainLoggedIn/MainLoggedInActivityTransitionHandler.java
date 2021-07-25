package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

public class MainLoggedInActivityTransitionHandler {
    private final String transition;

    public MainLoggedInActivityTransitionHandler(String transition){
        this.transition=transition;
    }

    public String getTransition() {
        return transition;
    }
}
