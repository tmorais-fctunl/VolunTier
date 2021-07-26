package pt.unl.fct.di.aldeia.apdc2021.ui.event.ReadQRCode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetQRRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.SendQRCodeRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EarnedAmmount;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode.QRCodeResult;

public class ReadQRCodeViewModel extends ViewModel {
    private final SendQRCodeRepository repository;
    private final MutableLiveData<DefaultResult> getQRCodeParticipantResult = new MutableLiveData<>();
    private final MutableLiveData<ReadQRLeaveResult> getQRCodeLeaveResult = new MutableLiveData<>();
    private final Executor executor;

    ReadQRCodeViewModel(SendQRCodeRepository repository, Executor executor) {
        this.executor = executor;
        this.repository = repository;
    }

    LiveData<DefaultResult> getParticipantQRCodeResult() {
        return getQRCodeParticipantResult;
    }
    LiveData<ReadQRLeaveResult> getLeaveQRCodeResult() {
        return getQRCodeLeaveResult;
    }

    public void sendParticipantQRCode(SendQRCodeData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.sendConfirmationQRCode(data);
                if (result instanceof Result.Success) {
                    getQRCodeParticipantResult.postValue(new DefaultResult(R.string.send_qr_success, null));
                } else {
                    getQRCodeParticipantResult.postValue(new DefaultResult(null,R.string.send_qr_failed));
                }
            }
        });
    }

    public void sendLeaveQRCode(SendQRCodeData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<EarnedAmmount> result = repository.sendLeaveQRCode(data);
                if (result instanceof Result.Success) {
                    getQRCodeLeaveResult.postValue(new ReadQRLeaveResult(((Result.Success<EarnedAmmount>) result).getData(), null));
                } else {
                    getQRCodeLeaveResult.postValue(new ReadQRLeaveResult(null,R.string.send_qr_failed));
                }
            }
        });
    }
}
