package pt.unl.fct.di.aldeia.apdc2021.ui.event.ReadQRCode;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetQRRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LeaveQRCodeDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ParticipantQRCodeDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.SendLeaveConfirmationDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.SendPresenceConfirmationDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.SendQRCodeRepository;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode.QRCodeViewModel;

public class ReadQRCodeViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public ReadQRCodeViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReadQRCodeViewModel.class)) {
            return (T) new ReadQRCodeViewModel(SendQRCodeRepository.getInstance(new SendPresenceConfirmationDataSource(), new SendLeaveConfirmationDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
