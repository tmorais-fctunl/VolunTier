package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;

public class GetEventViewModel extends ViewModel {


    private final MutableLiveData<GetEventResult> getEventResult = new MutableLiveData<>();
    private final MutableLiveData<UpdateEventFormState> updateEventFormState = new MutableLiveData<>();
    private final MutableLiveData<UpdateEventResult> updateEventResult = new MutableLiveData<>();
    private final GetEventRepository getEventRepository;
    private final String[] categories = {"CUIDAR_DE_ANIMAIS","ENSINAR_IDIOMAS","ENSINAR_MUSICA",
            "INICIATIVAS_AMBIENTAIS","DESASTRES_AMBIENTAIS","COMUNICACAO_DIGITAL","AUXILIO_DE_DOENTES",
            "AJUDAR_PORTADORES_DE_DEFICIENCIA","AJUDA_DESPORTIVA","AJUDA_EMPRESARIAL","AJUDA_A_CRIANCAS",
            "AJUDA_A_IDOSOS","AJUDA_A_SEM_ABRIGO","PROMOCAO,INTERNACIONAL","PROTECAO_CIVIL","SOCIAL","RECICLAGEM","CONSTRUCAO"};

    private final Executor executor;

    GetEventViewModel(GetEventRepository getEventRepository, Executor executor) {
        this.executor = executor;
        this.getEventRepository = getEventRepository;
    }

    LiveData<UpdateEventFormState> getUpdateEventFormState() {
        return updateEventFormState;
    }

    LiveData<GetEventResult> getGetEventResult() {
        return getEventResult;
    }

    LiveData<UpdateEventResult> getUpdateEventResult() {
        return updateEventResult;
    }

    public void getEvent(String email, String token,String event_id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<EventFullData> result = getEventRepository.getEvent(email, token,event_id);
                if (result instanceof Result.Success) {
                    getEventResult.postValue(new GetEventResult(((Result.Success<EventFullData>) result).getData(),null));
                } else {
                    getEventResult.postValue(new GetEventResult(null,R.string.get_event_failed));
                }
            }
        });
    }

    public void updateEvent(EventUpdateData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventRepository.updateEvent(event);
                if (result instanceof Result.Success) {
                    updateEventResult.postValue(new UpdateEventResult(R.string.update_event_success,null));
                } else {
                    updateEventResult.postValue(new UpdateEventResult(null,R.string.update_event_failed));
                }
            }
        });
    }

    public void updateEventDataChanged(String contact ,String startDate, String endDate, String category, String website, String facebook,String instagram , String twitter, String description) {
        boolean somethingWrong=false;
        Integer contactError=null;
        Integer startDateError=null;
        Integer endDateError=null;
        Integer categoryError=null;
        Integer websiteError=null;
        Integer descriptionError=null;
        Integer twitterError=null;
        Integer instagramError=null;
        Integer facebookError=null;
        if (!isContactValid(contact)) {
            somethingWrong=true;
            contactError=R.string.update_event_contactError;
        }if (!isDateValid(startDate)) {
            somethingWrong=true;
            startDateError=R.string.update_event_dateError;
        }if (!isDateValid(endDate)) {
            somethingWrong=true;
            endDateError=R.string.update_event_dateError;
        }if (!isCategoryValid(category)) {
            somethingWrong = true;
            categoryError=R.string.update_event_categoryError;
        }
        if(!isWebsiteValid(website)) {
            somethingWrong = true;
            websiteError=R.string.update_event_websiteError;
        }
        if(!isWebsiteValid(facebook)) {
            somethingWrong = true;
            facebookError=R.string.update_event_websiteError;
        }
        if(!isWebsiteValid(instagram)) {
            somethingWrong = true;
            instagramError=R.string.update_event_websiteError;
        }
        if(!isWebsiteValid(twitter)) {
            somethingWrong = true;
            twitterError=R.string.update_event_websiteError;
        }
        if(!isDescriptionValid(description)) {
            somethingWrong=true;
            descriptionError=R.string.update_event_commentError;
        }
        if(!somethingWrong){
            updateEventFormState.postValue(new UpdateEventFormState(true));
        }
        else {
            updateEventFormState.postValue(new UpdateEventFormState(contactError, startDateError,endDateError, categoryError, websiteError,
                     facebookError,instagramError,twitterError , descriptionError));
        }
    }


    private boolean isContactValid(String contact) {
        return contact.equals("")||contact.matches("([+][0-9]{2,3}\\s)?[789][0-9]{8}");
    }

    //TODO
    private boolean isDateValid(String date) {
        return true;
    }

    private boolean isCategoryValid(String category) {
        boolean valid = false;
        if(category.equals("")) {
            valid = true;
        }
        for(int i = 0; i< categories.length && !valid; i++) {
            if(categories[i].equals(category)) {
                valid = true;
            }
        }
        return valid;
    }

    private boolean isWebsiteValid(String website) {
        return website.length()<120 || website.equals("");
    }

    private boolean isDescriptionValid(String description) {
        return description.length()<500 || description.equals("");
    }
}
