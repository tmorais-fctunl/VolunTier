package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RateRouteRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RefreshTokenRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteRatingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshResult;

public class RateRouteViewModel extends ViewModel {
    private final RateRouteRepository routeRepository;
    private MutableLiveData<RateRoutResult> rateRouteResult = new MutableLiveData<>();
    private final Executor executor;

    public RateRouteViewModel(RateRouteRepository refreshRepository, Executor executor) {
        this.executor = executor;
        this.routeRepository = refreshRepository;
    }

    public MutableLiveData<RateRoutResult> getRateRouteResult() {
        return rateRouteResult;
    }

    public void rateRoute(RouteRatingData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = routeRepository.rateRoute(data.getEmail(), data.getToken(),data.getRoute_id(), data.getRating());
                if (result instanceof Result.Success) {
                    rateRouteResult.postValue(new RateRoutResult(R.string.route_rating_success,null));
                } else {
                    rateRouteResult.postValue(new RateRoutResult(null,R.string.route_rating_failed));
                }
            }
        });
    }
}