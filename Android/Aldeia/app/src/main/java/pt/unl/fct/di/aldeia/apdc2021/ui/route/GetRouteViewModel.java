package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RefreshTokenRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LeaveRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshResult;

public class GetRouteViewModel extends ViewModel {
    private final GetRouteRepository routeRepository;
    private final MutableLiveData<GetRouteResult> getRouteResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> deleteRouteResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> participateRouteResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> leaveRouteResult = new MutableLiveData<>();
    private final Executor executor;

    GetRouteViewModel(GetRouteRepository refreshRepository, Executor executor) {
        this.executor = executor;
        this.routeRepository = refreshRepository;
    }

    public MutableLiveData<GetRouteResult> getGetRouteResult() {
        return getRouteResult;
    }

    public MutableLiveData<DefaultResult> getDeleteRouteResult() {
        return deleteRouteResult;
    }

    public MutableLiveData<DefaultResult> getParticipateRouteResult() {
        return participateRouteResult;
    }

    public MutableLiveData<DefaultResult> getLeaveRouteResult() {
        return leaveRouteResult;
    }


    public void getRoute(GetRouteData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<GetRouteReply> result = routeRepository.getRoute(data);
                if (result instanceof Result.Success) {
                    getRouteResult.postValue(new GetRouteResult(((Result.Success<GetRouteReply>) result).getData(),null));
                } else {
                    getRouteResult.postValue(new GetRouteResult(null,R.string.route_retrieval_failed));
                }
            }
        });
    }

    public void deleteRoute(GetRouteData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = routeRepository.deleteRoute(data);
                if (result instanceof Result.Success) {
                    deleteRouteResult.postValue(new DefaultResult(R.string.delete_route_success,null));
                } else {
                    deleteRouteResult.postValue(new DefaultResult(null,R.string.delete_route_failed));
                }
            }
        });
    }

    public void participateRoute(GetRouteData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = routeRepository.participateRoute(data);
                if (result instanceof Result.Success) {
                    participateRouteResult.postValue(new DefaultResult(R.string.event_participation_succeeded,null));
                } else {
                    participateRouteResult.postValue(new DefaultResult(null,R.string.event_participation_failed));
                }
            }
        });
    }

    public void leaveRoute(LeaveRouteData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = routeRepository.leaveRoute(data);
                if (result instanceof Result.Success) {
                    leaveRouteResult.postValue(new DefaultResult(R.string.event_participation_succeeded,null));
                } else {
                    leaveRouteResult.postValue(new DefaultResult(null,R.string.event_participation_failed));
                }
            }
        });
    }
}
