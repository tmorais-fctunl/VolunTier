package pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetQRRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants.GetEventParticipantsResult;

public class QRCodeViewModel extends ViewModel {
    private final GetQRRepository repository;
    private final MutableLiveData<QRCodeResult> getQRCodeParticipantResult = new MutableLiveData<>();
    private final MutableLiveData<QRCodeResult> getQRCodeLeaveResult = new MutableLiveData<>();
    private final Executor executor;

    QRCodeViewModel(GetQRRepository repository, Executor executor) {
        this.executor = executor;
        this.repository = repository;
    }

    LiveData<QRCodeResult> getParticipantQRCOdeResult() {
        return getQRCodeParticipantResult;
    }
    LiveData<QRCodeResult> getLeaveQRCOdeResult() {
        return getQRCodeLeaveResult;
    }

    public void getParticipantQRCode(GetEventData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<QRCode> result = repository.participantQRCode(event);
                if (result instanceof Result.Success) {
                    getQRCodeParticipantResult.postValue(new QRCodeResult(((Result.Success<QRCode>) result).getData(), null));
                } else {
                    getQRCodeParticipantResult.postValue(new QRCodeResult(null, R.string.failed_get_qr));
                }
            }
        });
    }

    public void getLeaveQRCode(GetEventData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<QRCode> result = repository.leaveQRCode(event);
                if (result instanceof Result.Success) {
                    getQRCodeLeaveResult.postValue(new QRCodeResult(((Result.Success<QRCode>) result).getData(), null));
                } else {
                    getQRCodeLeaveResult.postValue(new QRCodeResult(null, R.string.failed_get_qr));
                }
            }
        });
    }
}
