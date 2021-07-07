package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginFormState;

public class GetEventActivity extends AppCompatActivity {

    private UserLocalStore storage;
    private GetEventViewModel getEventViewModel;
    private GetEventActivity mActivity;
    private EventFullData initialEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String event_id = null;
        if (extras != null) {
            event_id = extras.getString("event_id");
        }
        setContentView(R.layout.event_page);
        storage= new UserLocalStore(this);
        getEventViewModel = new ViewModelProvider(this, new GetEventViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(GetEventViewModel.class);
        mActivity=this;
        final EditText eventNameEditText = findViewById(R.id.event_page_name);
        final EditText eventStartDateEditText = findViewById(R.id.event_page_start);
        final EditText eventEndDateEditText = findViewById(R.id.event_page_end);
        final EditText eventOwnerEmailEditText = findViewById(R.id.event_page_owner_email);
        final EditText eventOwnerContactEditText = findViewById(R.id.event_page_owner_contact);
        final EditText eventDescriptionEditText = findViewById(R.id.event_page_description);
        final EditText eventCategoryEditText = findViewById(R.id.event_page_category);
        final EditText eventCapacityEditText = findViewById(R.id.event_page_capacity);
        final EditText eventWebsiteEditText = findViewById(R.id.event_page_website);
        final EditText eventFacebookEditText = findViewById(R.id.event_page_facebook);
        final EditText eventTwitterEditText = findViewById(R.id.event_page_twitter);
        final EditText eventInstagramEditText = findViewById(R.id.event_page_instagram);
        final Switch eventProfileSwitch = findViewById(R.id.event_switch_profile);
        final Button eventDeleteButton =findViewById(R.id.event_page_delete);
        final Button eventChangeLocationButton =findViewById(R.id.event_page_location);
        final Button eventChatButton =findViewById(R.id.event_page_comments);
        final Button eventSaveButton =findViewById(R.id.event_page_save);
        final Button eventCancelButton =findViewById(R.id.event_page_cancel);

        //TODO form states

        UserAuthenticated user = storage.getLoggedInUser();
        getEventViewModel.getEvent(user.getEmail(), user.getTokenID(), event_id);

        getEventViewModel.getGetEventResult().observe(this, new Observer<GetEventResult>() {
            @Override
            public void onChanged(@Nullable GetEventResult getEventResult) {
                if (getEventResult == null) {
                    return;
                }
                if (getEventResult.getError() != null) {
                    Toast.makeText(mActivity, "Event Retrieval Unsuccessful", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (getEventResult.getSuccess() != null) {
                    EventFullData eventFullData = getEventResult.getSuccess();
                    initialEvent=eventFullData;
                    eventNameEditText.setText(eventFullData.getName());
                    eventStartDateEditText.setText(eventFullData.getStartDate());
                    eventEndDateEditText.setText(eventFullData.getEndDate());
                    eventOwnerEmailEditText.setText(eventFullData.getOwnerEmail());
                    eventOwnerContactEditText.setText(eventFullData.getContact());
                    eventDescriptionEditText.setText(eventFullData.getDescription());
                    eventCategoryEditText.setText(eventFullData.getCategory());
                    eventCapacityEditText.setText(String.valueOf(eventFullData.getCapacity()));
                    eventWebsiteEditText.setText(eventFullData.getWebsite());
                    eventFacebookEditText.setText(eventFullData.getFacebook());
                    eventTwitterEditText.setText(eventFullData.getTwitter());
                    eventInstagramEditText.setText(eventFullData.getInstagram());
                    switch(eventFullData.getProfile().toUpperCase()){
                        case "PRIVATE":
                            eventProfileSwitch.setChecked(true);
                            break;
                        default:
                            eventProfileSwitch.setChecked(false);
                            break;
                    }
                }
            }
        });

        //TODO
        eventCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getEventViewModel.getUpdateEventResult().observe(this, new Observer<UpdateEventResult>() {
            @Override
            public void onChanged(@Nullable UpdateEventResult updateEventResult) {
                if (updateEventResult == null) {
                    return;
                }
                if (updateEventResult.getError() != null) {
                    Toast.makeText(mActivity, "Event Update Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (updateEventResult.getSuccess() != null) {
                    Toast.makeText(mActivity, "Event Update Successful", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        getEventViewModel.getUpdateEventFormState().observe(this, new Observer<UpdateEventFormState>() {
            @Override
            public void onChanged(@Nullable UpdateEventFormState updateEventFormState) {
                if (updateEventFormState == null) {
                    return;
                }
                eventSaveButton.setEnabled(updateEventFormState.isDataValid());
                if (!eventOwnerContactEditText.getText().toString().equals("") && updateEventFormState.getContactError() != null) {
                    eventOwnerContactEditText.setError(getString(updateEventFormState.getContactError()));
                }
                if (!eventStartDateEditText.getText().toString().equals("") && updateEventFormState.getStartDateError() != null) {
                    eventStartDateEditText.setError(getString(updateEventFormState.getStartDateError()));
                }
                if (!eventEndDateEditText.getText().toString().equals("") && updateEventFormState.getEndDateError() != null) {
                    eventEndDateEditText.setError(getString(updateEventFormState.getEndDateError()));
                }
                if (!eventCategoryEditText.getText().toString().equals("") && updateEventFormState.getCategoryError() != null) {
                    eventCategoryEditText.setError(getString(updateEventFormState.getCategoryError()));
                }
                if (!eventWebsiteEditText.getText().toString().equals("") && updateEventFormState.getWebsiteError() != null) {
                    eventWebsiteEditText.setError(getString(updateEventFormState.getWebsiteError()));
                }
                if (!eventFacebookEditText.getText().toString().equals("") && updateEventFormState.getFacebookError() != null) {
                    eventFacebookEditText.setError(getString(updateEventFormState.getFacebookError()));
                }
                if (!eventInstagramEditText.getText().toString().equals("") && updateEventFormState.getInstagramError() != null) {
                    eventInstagramEditText.setError(getString(updateEventFormState.getInstagramError()));
                }
                if (!eventTwitterEditText.getText().toString().equals("") && updateEventFormState.getTwitterError() != null) {
                    eventTwitterEditText.setError(getString(updateEventFormState.getTwitterError()));
                }
                if (!eventDescriptionEditText.getText().toString().equals("") && updateEventFormState.getDescriptionError() != null) {
                    eventDescriptionEditText.setError(getString(updateEventFormState.getDescriptionError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                getEventViewModel.updateEventDataChanged(eventOwnerContactEditText.getText().toString(),
                        eventStartDateEditText.getText().toString(),eventEndDateEditText.getText().toString(),
                        eventCategoryEditText.getText().toString(),eventWebsiteEditText.getText().toString(),
                        eventFacebookEditText.getText().toString(),eventInstagramEditText.getText().toString(),
                        eventTwitterEditText.getText().toString(),eventDescriptionEditText.getText().toString());
            }
        };
        eventOwnerContactEditText.addTextChangedListener(afterTextChangedListener);
        eventStartDateEditText.addTextChangedListener(afterTextChangedListener);
        eventEndDateEditText.addTextChangedListener(afterTextChangedListener);
        eventCategoryEditText.addTextChangedListener(afterTextChangedListener);
        eventWebsiteEditText.addTextChangedListener(afterTextChangedListener);
        eventFacebookEditText.addTextChangedListener(afterTextChangedListener);
        eventInstagramEditText.addTextChangedListener(afterTextChangedListener);
        eventTwitterEditText.addTextChangedListener(afterTextChangedListener);
        eventDescriptionEditText.addTextChangedListener(afterTextChangedListener);

        eventSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUpdateData updatedEvent= new EventUpdateData(user.getEmail(), user.getTokenID(),initialEvent.getEventId(),initialEvent.getLocation(),eventStartDateEditText.getText().toString(),eventEndDateEditText.getText().toString(),eventOwnerEmailEditText.getText().toString(),eventOwnerContactEditText.getText().toString(),eventDescriptionEditText.getText().toString(),eventCategoryEditText.getText().toString(),Integer.parseInt(eventCapacityEditText.getText().toString()),eventWebsiteEditText.getText().toString(),eventFacebookEditText.getText().toString(),eventInstagramEditText.getText().toString(),eventTwitterEditText.getText().toString());
                getEventViewModel.updateEvent(updatedEvent);
            }
        });

    }
}