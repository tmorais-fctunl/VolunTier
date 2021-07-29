package pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetQRRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LeaveQRCodeDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ParticipantQRCodeDataSource;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants.GetEventParticipantsViewModel;

public class QRCodeViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public QRCodeViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QRCodeViewModel.class)) {
            return (T) new QRCodeViewModel(GetQRRepository.getInstance(new ParticipantQRCodeDataSource(), new LeaveQRCodeDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
