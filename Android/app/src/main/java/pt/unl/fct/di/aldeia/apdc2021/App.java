package pt.unl.fct.di.aldeia.apdc2021;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
