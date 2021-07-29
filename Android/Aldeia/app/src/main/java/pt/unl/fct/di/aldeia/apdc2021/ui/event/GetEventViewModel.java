package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RemoveParticipantFromEventData;

public class GetEventViewModel extends ViewModel {


    private final MutableLiveData<GetEventResult> getEventResult = new MutableLiveData<>();
    private final MutableLiveData<UpdateEventFormState> updateEventFormState = new MutableLiveData<>();
    private final MutableLiveData<UpdateEventResult> updateEventResult = new MutableLiveData<>();
    private final GetEventRepository getEventRepository;
    private double[] eventCoordinates;
    private final MutableLiveData<DefaultResult> removeEventResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> participateInEventResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> leaveEventResult = new MutableLiveData<>();
    private final Executor executor;

    GetEventViewModel(GetEventRepository getEventRepository, Executor executor) {
        this.executor = executor;
        this.getEventRepository = getEventRepository;
    }

    LiveData<DefaultResult> getRemoveEventResult() {
        return removeEventResult;
    }

    LiveData<UpdateEventFormState> getUpdateEventFormState() {
        return updateEventFormState;
    }

    LiveData<GetEventResult> getGetEventResult() {
        return getEventResult;
    }

    LiveData<DefaultResult> getParticipationResult() {
        return participateInEventResult;
    }

    LiveData<UpdateEventResult> getUpdateEventResult() {
        return updateEventResult;
    }

    public void setEventCoordinates (double[] latLng) {
        eventCoordinates = latLng;
    }

    public double[] getEventCoordinates() {
        return eventCoordinates;
    }

    LiveData<DefaultResult> getLeaveEventResult() {
        return leaveEventResult;
    }

    public void getEvent(String email, String token, String event_id) {
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

    public void removeEvent(String email,String token, String event_id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventRepository.removeEvent(email, token, event_id);
                if (result instanceof Result.Success) {
                    removeEventResult.postValue(new DefaultResult(R.string.RemoveEvent_success, null));
                } else {
                    removeEventResult.postValue(new DefaultResult(null, R.string.RemoveEvent_failed));
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
                    updateEventResult.postValue(new UpdateEventResult(event,null));
                } else {
                    updateEventResult.postValue(new UpdateEventResult(null,R.string.update_event_failed));
                }
            }
        });
    }

    public void participateInEvent(GetEventData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventRepository.participateInEvent(event);
                if (result instanceof Result.Success) {
                    participateInEventResult.postValue(new DefaultResult(R.string.event_participation_succeeded,null));
                } else {
                    participateInEventResult.postValue(new DefaultResult(null,R.string.event_participation_failed));
                }
            }
        });
    }

    public void leaveEvent(RemoveParticipantFromEventData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventRepository.leaveEvent(data);
                if (result instanceof Result.Success) {
                    leaveEventResult.postValue(new DefaultResult(R.string.event_participation_succeeded,null));
                } else {
                    leaveEventResult.postValue(new DefaultResult(null,R.string.event_participation_failed));
                }
            }
        });
    }

    public void updateEventDataChanged(String contact ,String startDate, String endDate, String website, String facebook,String instagram , String twitter, String description) {
        boolean somethingWrong=false;
        Integer contactError=null;
        Integer dateError=null;
        Integer websiteError=null;
        Integer descriptionError=null;
        Integer twitterError=null;
        Integer instagramError=null;
        Integer facebookError=null;
        if (!isContactValid(contact)) {
            somethingWrong=true;
            contactError=R.string.update_event_contactError;
        }if (!isDateValid(startDate, endDate)) {
            somethingWrong=true;
            dateError=R.string.update_event_dateError;
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
            updateEventFormState.postValue(new UpdateEventFormState(contactError, dateError, websiteError,
                     facebookError,instagramError,twitterError, descriptionError));
        }
    }


    private boolean isContactValid(String contact) {
        return contact.equals("")||contact.matches("([+][0-9]{2,3}\\s)?[789][0-9]{8}");
    }

    private boolean isDateValid(String startDate, String endDate) {
        if (startDate.length()<17 || endDate.length()<17 ) {
            return true;
        }
        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String sDate = startDate.substring(0,startDate.indexOf("T"));
        sDate = sDate.concat("-").concat(startDate.substring(startDate.indexOf("T")+1, startDate.length()-4));
        String eDate = endDate.substring(0,endDate.indexOf("T"));
        eDate = eDate.concat("-").concat(endDate.substring(endDate.indexOf("T")+1, endDate.length()-4));
        try {
            int a = 2;
            Date sD = formatD.parse(sDate);
            Date eD = formatD.parse(eDate);
            if(sD.compareTo(eD) > 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean isWebsiteValid(String website) {
        return website.length()<120 || website.equals("");
    }

    private boolean isDescriptionValid(String description) {
        return description.length()<500 || description.equals("");
    }


}
