package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RemoveParticipantFromEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.EventChatActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants.GetEventParticipantsActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode.QRCodeActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.ReadQRCode.ReadQRCodeActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.GeoHashUtil;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.RoomViewModel;

public class GetEventActivity extends AppCompatActivity {

    private final int CHANGE_LOC = 7;

    private UserLocalStore storage;
    private GetEventViewModel getEventViewModel;
    private GetEventActivity mActivity;
    private EventFullData initialEvent;
    private RoomViewModel roomViewModel;
    private int hour, min;

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
        roomViewModel=new RoomViewModel(getApplication());
        mActivity=this;
        final EditText eventNameEditText = findViewById(R.id.route_page_name);
        final Button eventStartDayButton = findViewById(R.id.event_page_startDate);
        final Button eventEndDayButton = findViewById(R.id.event_page_endDate);
        final Button eventStartHourButton = findViewById(R.id.event_page_startTime);
        final Button eventEndHourButton = findViewById(R.id.event_page_endTime);
        final EditText eventOwnerEmailEditText = findViewById(R.id.route_page_creator);
        final EditText eventOwnerContactEditText = findViewById(R.id.event_page_owner_contact);
        final EditText eventDescriptionEditText = findViewById(R.id.route_page_description);
        final Spinner eventCategorySpinner = findViewById(R.id.event_page_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategorySpinner.setAdapter(adapter);
        final EditText eventCapacityEditText = findViewById(R.id.route_page_rating);
        final EditText eventWebsiteEditText = findViewById(R.id.event_page_website);
        final EditText eventFacebookEditText = findViewById(R.id.event_page_facebook);
        final EditText eventTwitterEditText = findViewById(R.id.event_page_twitter);
        final EditText eventInstagramEditText = findViewById(R.id.event_page_instagram);
        final Switch eventProfileSwitch = findViewById(R.id.event_switch_profile);
        final Button eventDeleteButton =findViewById(R.id.route_delete_button);
        final Button eventChangeLocationButton =findViewById(R.id.event_page_location);
        final Button eventChatButton =findViewById(R.id.route_page_comments);
        final Button eventSaveButton =findViewById(R.id.route_page_save);
        final Button eventCancelButton =findViewById(R.id.route_page_cancel);
        final Button getQRCodes =findViewById(R.id.eventGetQRCode);
        final EditText eventOwnerNameEditText = findViewById(R.id.route_page_creation_date);
        final EditText eventParticipantsEditText = findViewById(R.id.route_page_participants);
        final Spinner eventDifficulty = findViewById(R.id.event_page_difficulty);
        final ImageButton eventParticipants=findViewById(R.id.event_participants_button);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.difficulties));
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventDifficulty.setAdapter(adapter2);


        final ProgressBar eventProgressBar=findViewById(R.id.routeProgressBar);
        eventProgressBar.setVisibility(View.VISIBLE);

        UserAuthenticated user = storage.getLoggedInUser();
        getEventViewModel.getEvent(user.getEmail(), user.getTokenID(), event_id);

        getEventViewModel.getGetEventResult().observe(this, new Observer<GetEventResult>() {
            @Override
            public void onChanged(@Nullable GetEventResult getEventResult) {
                if (getEventResult == null) {
                    return;
                }
                if (getEventResult.getError() != null) {
                    Toast.makeText(mActivity, getEventResult.getError(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (getEventResult.getSuccess() != null) {
                    EventFullData eventFullData = getEventResult.getSuccess();
                    getEventViewModel.setEventCoordinates(eventFullData.getLocation());
                    getEventViewModel.setEventCoordinates(eventFullData.getLocation());
                    initialEvent=eventFullData;
                    eventNameEditText.setText(eventFullData.getName());
                    String startDate = eventFullData.getStartDate();
                    int pos = startDate.indexOf("T")+1;
                    String day = startDate.substring(0,pos);
                    String time = startDate.substring(pos);
                    eventStartDayButton.setText(day);
                    eventStartHourButton.setText(time);
                    String endDate = eventFullData.getEndDate();
                    pos = endDate.indexOf("T")+1;
                    day = endDate.substring(0,pos);
                    time = endDate.substring(pos);
                    eventEndDayButton.setText(day);
                    eventEndHourButton.setText(time);
                    eventOwnerEmailEditText.setText(eventFullData.getOwnerEmail());
                    eventOwnerContactEditText.setText(eventFullData.getContact());
                    eventDescriptionEditText.setText(eventFullData.getDescription());
                    String category = eventFullData.getCategory();
                    if (category != null) {
                        switch (category) {
                            case "AJUDA_A_CRIANCAS":
                                category = "Ajuda a Criancas";
                                break;
                            case "AJUDA_A_IDOSOS":
                                category = "Ajuda a Idosos";
                                break;
                            case "AJUDA_A_SEM_ABRIGO":
                                category = "Ajuda a Sem AbrigoAJUDA_A_SEM_ABRIGO";
                                break;
                            case "AJUDA_DESPORTIVA":
                                category = "Ajuda Desportiva";
                                break;
                            case "AJUDA_EMPRESARIAL":
                                category = "Ajuda Empresarial";
                                break;
                            case "AJUDAR_PORTADORES_DE_DEFICIENCIA":
                                category = "Ajudar Portadores de Deficiencia";
                                break;
                            case "AUXILIO_DE_DOENTES":
                                category = "Auxilio de Doentes";
                                break;
                            case "COMUNICACAO_DIGITAL":
                                category = "Comunicacao Digital";
                                break;
                            case "CONSTRUCAO":
                                category = "Construcao";
                                break;
                            case "CUIDAR_DE_ANIMAIS":
                                category = "Cuidar de Animais";
                                break;
                            case "DESASTRES_AMBIENTAIS":
                                category = "Desastres Ambientais";
                                break;
                            case "ENSINAR_IDIOMAS":
                                category = "Ensinar Idiomas";
                                break;
                            case "ENSINAR_MUSICA":
                                category = "Ensinar Musica";
                                break;
                            case "INICIATIVAS_AMBIENTAIS":
                                category = "Iniciativas Ambientais";
                                break;
                            case "INTERNACIONAL":
                                category = "Internacional";
                                break;
                            case "PROMOCAO":
                                category = "Promocao";
                                break;
                            case "PROTECAO_CIVIL":
                                category = "Protecao Civil";
                                break;
                            case "RECICLAGEM":
                                category = "Reciclagem";
                                break;
                            case "SOCIAL":
                                category = "Social";
                                break;
                            default:
                                break;
                        }
                        eventCategorySpinner.setSelection(adapter.getPosition(category));
                    }
                    eventDifficulty.setSelection(adapter2.getPosition(String.valueOf(eventFullData.getDifficulty())));
                    eventOwnerNameEditText.setText(eventFullData.getOwnerName());
                    eventParticipantsEditText.setText(String.valueOf(eventFullData.getNumParticipants()));
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
                    eventOwnerEmailEditText.setClickable(false);
                    eventOwnerEmailEditText.setFocusable(false);
                    eventOwnerNameEditText.setClickable(false);
                    eventOwnerNameEditText.setFocusable(false);
                    eventParticipantsEditText.setClickable(false);
                    eventParticipantsEditText.setFocusable(false);
                    if(!eventFullData.getStatus().equals("OWNER")){
                        eventDifficulty.setClickable(false);
                        eventDifficulty.setFocusable(false);
                        eventCategorySpinner.setClickable(false);
                        eventCategorySpinner.setFocusable(false);
                        eventDifficulty.setClickable(false);
                        getQRCodes.setText("Send Code");
                        eventNameEditText.setClickable(false);
                        eventNameEditText.setFocusable(false);
                        eventStartDayButton.setClickable(false);
                        eventStartHourButton.setClickable(false);
                        eventEndDayButton.setClickable(false);
                        eventEndHourButton.setClickable(false);
                        eventOwnerEmailEditText.setClickable(false);
                        eventOwnerEmailEditText.setFocusable(false);
                        eventOwnerContactEditText.setClickable(false);
                        eventOwnerContactEditText.setFocusable(false);
                        eventDescriptionEditText.setClickable(false);
                        eventDescriptionEditText.setFocusable(false);
                        eventCategorySpinner.setClickable(false);
                        eventCapacityEditText.setClickable(false);
                        eventCapacityEditText.setFocusable(false);
                        eventWebsiteEditText.setClickable(false);
                        eventWebsiteEditText.setFocusable(false);
                        eventFacebookEditText.setClickable(false);
                        eventFacebookEditText.setFocusable(false);
                        eventTwitterEditText.setClickable(false);
                        eventTwitterEditText.setFocusable(false);
                        eventInstagramEditText.setClickable(false);
                        eventInstagramEditText.setFocusable(false);
                        eventProfileSwitch.setClickable(false);
                        eventSaveButton.setClickable(false);
                        eventSaveButton.setVisibility(View.GONE);
                        eventChangeLocationButton.setVisibility(View.GONE);
                        eventChangeLocationButton.setClickable(false);
                        eventDifficulty.setClickable(false);
                    }
                    if(eventFullData.getStatus().equals("PARTICIPANT")){
                        eventDeleteButton.setText(R.string.event_page_leave);
                    }
                    if(eventFullData.getStatus().equals("NON_PARTICIPANT")){
                        eventDeleteButton.setText(R.string.event_page_join);
                    }
                    if(eventFullData.getStatus().equals("PENDING")){
                        eventDeleteButton.setText(R.string.event_page_join);
                        eventDeleteButton.setClickable(false);
                    }
                    eventProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        eventCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        eventChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EventChatActivity.class);
                intent.putExtra("eventID",initialEvent.getEventId());
                intent.putExtra("status",initialEvent.getStatus());
                startActivity(intent);
            }
        });

        eventParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GetEventParticipantsActivity.class);
                intent.putExtra("eventID",initialEvent.getEventId());
                startActivity(intent);
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
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                }
                if (updateEventResult.getSuccess() != null) {
                    Toast.makeText(mActivity, "Event Update Successful", Toast.LENGTH_SHORT).show();
                    EventUpdateData update= updateEventResult.getSuccess();
                    EventEntity event = new EventEntity(update.getEventId(),update.getLocation()[0],update.getLocation()[1],update.getEvent_name(),update.getCapacity(),
                            update.getStartDate(),update.getEndDate(), GeoHashUtil.convertCoordsToGeoHashLowPrecision(update.getLocation()[0],update.getLocation()[1]));
                    roomViewModel.updateEvent(event);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("operation","Update");
                    returnIntent.putExtra("id",event.getEvent_id());
                    returnIntent.putExtra("lat",event.getLatitude());
                    returnIntent.putExtra("lon",event.getLongitude());
                    setResult(Activity.RESULT_OK,returnIntent);
                }
                finish();
            }
        });

        getQRCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(initialEvent.getStatus().equals("OWNER")){
                    Intent intent = new Intent(mActivity, QRCodeActivity.class);
                    intent.putExtra("event_id", initialEvent.getEventId());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(mActivity, ReadQRCodeActivity.class);
                    startActivity(intent);
                }

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
                if (updateEventFormState.getDateError() != null) {
                    eventStartDayButton.setError(getString(updateEventFormState.getDateError()));
                    eventStartHourButton.setError(getString(updateEventFormState.getDateError()));
                    eventEndDayButton.setError(getString(updateEventFormState.getDateError()));
                    eventEndHourButton.setError(getString(updateEventFormState.getDateError()));
                }else{
                    eventStartDayButton.setError(null);
                    eventStartHourButton.setError(null);
                    eventEndDayButton.setError(null);
                    eventEndHourButton.setError(null);
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
                        eventStartDayButton.getText().toString() + eventStartHourButton.getText().toString(),
                        eventEndDayButton.getText().toString() + eventEndHourButton.getText().toString(),
                        eventWebsiteEditText.getText().toString(),
                        eventFacebookEditText.getText().toString(),eventInstagramEditText.getText().toString(),
                        eventTwitterEditText.getText().toString(),eventDescriptionEditText.getText().toString());
            }
        };
        eventOwnerContactEditText.addTextChangedListener(afterTextChangedListener);
        eventStartDayButton.addTextChangedListener(afterTextChangedListener);
        eventEndDayButton.addTextChangedListener(afterTextChangedListener);
        eventStartHourButton.addTextChangedListener(afterTextChangedListener);
        eventEndHourButton.addTextChangedListener(afterTextChangedListener);
        eventWebsiteEditText.addTextChangedListener(afterTextChangedListener);
        eventFacebookEditText.addTextChangedListener(afterTextChangedListener);
        eventInstagramEditText.addTextChangedListener(afterTextChangedListener);
        eventTwitterEditText.addTextChangedListener(afterTextChangedListener);
        eventDescriptionEditText.addTextChangedListener(afterTextChangedListener);


        eventChangeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EditLocationActivity.class);
                intent.putExtra("coords", initialEvent.getLocation());
                startActivityForResult(intent, CHANGE_LOC);
            }
        });



        eventSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacy="PUBLIC";
                if(eventProfileSwitch.isChecked()){
                    privacy="PRIVATE";
                }
                String category = eventCategorySpinner.getSelectedItem().toString();
                switch (category) {
                    case "Ajuda a Criancas":
                        category = "AJUDA_A_CRIANCAS";
                        break;
                    case "Ajuda a Idosos":
                        category = "AJUDA_A_IDOSOS";
                        break;
                    case "Ajuda a Sem Abrigo":
                        category = "AJUDA_A_SEM_ABRIGO";
                        break;
                    case "Ajuda Desportiva":
                        category = "AJUDA_DESPORTIVA";
                        break;
                    case "Ajuda Empresarial":
                        category = "AJUDA_EMPRESARIAL";
                        break;
                    case "Ajudar Portadores de Deficiencia":
                        category = "AJUDAR_PORTADORES_DE_DEFICIENCIA";
                        break;
                    case "Auxilio de Doentes":
                        category = "AUXILIO_DE_DOENTES";
                        break;
                    case "Comunicacao Digital":
                        category = "COMUNICACAO_DIGITAL";
                        break;
                    case "Construcao":
                        category = "CONSTRUCAO";
                        break;
                    case "Cuidar de Animais":
                        category = "CUIDAR_DE_ANIMAIS";
                        break;
                    case "Desastres Ambientais":
                        category = "DESASTRES_AMBIENTAIS";
                        break;
                    case "Ensinar Idiomas":
                        category = "ENSINAR_IDIOMAS";
                        break;
                    case "Ensinar Musica":
                        category = "ENSINAR_MUSICA";
                        break;
                    case "Iniciativas Ambientais":
                        category = "INICIATIVAS_AMBIENTAIS";
                        break;
                    case "Internacional":
                        category = "INTERNACIONAL";
                        break;
                    case "Promocao":
                        category = "PROMOCAO";
                        break;
                    case "Protecao Civil":
                        category = "PROTECAO_CIVIL";
                        break;
                    case "Reciclagem":
                        category = "RECICLAGEM";
                        break;
                    case "Social":
                        category = "SOCIAL";
                        break;
                    default:
                        break;
                }
                EventUpdateData updatedEvent= new EventUpdateData(user.getEmail(), user.getTokenID(),initialEvent.getEventId(),getEventViewModel.getEventCoordinates(),
                        eventNameEditText.getText().toString(),eventStartDayButton.getText().toString() + eventStartHourButton.getText().toString(),
                        eventEndDayButton.getText().toString() + eventEndHourButton.getText().toString(),
                        eventOwnerContactEditText.getText().toString(),
                        eventDescriptionEditText.getText().toString(),category,
                        Integer.parseInt(eventCapacityEditText.getText().toString()),eventWebsiteEditText.getText().toString(),
                        eventFacebookEditText.getText().toString(),eventInstagramEditText.getText().toString(),
                        eventTwitterEditText.getText().toString(), privacy, Integer.valueOf(eventDifficulty.getSelectedItem().toString()));
                getEventViewModel.updateEvent(updatedEvent);
            }
        });

        eventDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(initialEvent.getStatus().equals("OWNER")){
                    getEventViewModel.removeEvent(user.getEmail(), user.getTokenID(), initialEvent.getEventId());
                }
                else if (initialEvent.getStatus().equals("PARTICIPANT")) {
                    getEventViewModel.leaveEvent(new RemoveParticipantFromEventData(user.getEmail(), user.getTokenID(),user.getEmail() ,initialEvent.getEventId()));
                }
                else if(initialEvent.getStatus().equals("NON_PARTICIPANT")){
                    getEventViewModel.participateInEvent(new GetEventData(user.getEmail(), user.getTokenID(), initialEvent.getEventId()));
                }
            }
        });

        getEventViewModel.getRemoveEventResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult removeEventResult) {
                if (removeEventResult == null) {
                    return;
                }
                if (removeEventResult.getError() != null) {
                    Toast.makeText(mActivity, removeEventResult.getError(), Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                }
                if (removeEventResult.getSuccess() != null) {
                    Toast.makeText(mActivity, removeEventResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    roomViewModel.deleteEvent(initialEvent.getEventId());
                    roomViewModel.deleteCrossOverFromEventD(initialEvent.getEventId());
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("operation","Delete");
                    returnIntent.putExtra("id",initialEvent.getEventId());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

            }
        });

        getEventViewModel.getParticipationResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult participateEventResult) {
                if (participateEventResult == null) {
                    return;
                }
                if (participateEventResult.getError() != null) {
                    Toast.makeText(mActivity, participateEventResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if (participateEventResult.getSuccess() != null) {
                    Toast.makeText(mActivity, participateEventResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    int participants = Integer.parseInt(eventParticipantsEditText.getText().toString());
                        if(!initialEvent.getProfile().equals("PRIVATE")){
                            participants++;
                            eventParticipantsEditText.setText(String.valueOf(participants));
                            initialEvent.setStatus("PARTICIPANT");
                            eventDeleteButton.setText(R.string.event_page_leave);

                        }else{
                            initialEvent.setStatus("PENDING");
                            eventDeleteButton.setText(R.string.event_page_pending);
                            eventDeleteButton.setClickable(false);
                        }

                }
            }
        });

        getEventViewModel.getLeaveEventResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult leaveEventResult) {
                if (initialEvent.getStatus().equals("PARTICIPANT")) {
                    if (leaveEventResult == null) {
                        return;
                    }
                    if (leaveEventResult.getError() != null) {
                        Toast.makeText(mActivity, leaveEventResult.getError(), Toast.LENGTH_SHORT).show();
                    }
                    if (leaveEventResult.getSuccess() != null) {
                        Toast.makeText(mActivity, leaveEventResult.getSuccess(), Toast.LENGTH_SHORT).show();
                        int participants = Integer.parseInt(eventParticipantsEditText.getText().toString());
                        initialEvent.setStatus("NON_PARTICIPANT");
                        eventDeleteButton.setText(R.string.event_page_join);
                        participants--;
                        eventParticipantsEditText.setText(String.valueOf(participants));

                    }
                }
            }
        });

        eventStartHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h;
                        String m;
                        hour = hourOfDay;
                        min = minute;
                        if(hourOfDay < 10) {
                            h = "0"+String.valueOf(hourOfDay);
                        }
                        else {
                            h = String.valueOf(hourOfDay);
                        }
                        if(minute<10) {
                            m = "0"+String.valueOf(minute);
                        }
                        else {
                            m = String.valueOf(minute);
                        }
                        String startTimeText = h+":"+m+":00Z";
                        eventStartHourButton.setText(startTimeText);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, onTimeSetListener,hour, min, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        eventEndHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h;
                        String m;
                        hour = hourOfDay;
                        min = minute;
                        if(hourOfDay < 10) {
                            h = "0"+String.valueOf(hourOfDay);
                        }
                        else {
                            h = String.valueOf(hourOfDay);
                        }
                        if(minute<10) {
                            m = "0"+String.valueOf(minute);
                        }
                        else {
                            m = String.valueOf(minute);
                        }
                        String endTimeText = h+":"+m+":00Z";
                        eventEndHourButton.setText(endTimeText);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, onTimeSetListener,hour, min, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        eventStartDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthText;
                        String dayText;
                        if(month < 9) {
                            monthText = "0"+String.valueOf(month+1);
                        }
                        else {
                            monthText= String.valueOf(month+1);
                        }
                        if (dayOfMonth<10) {
                            dayText = "0"+String.valueOf(dayOfMonth);
                        }
                        else {
                            dayText = String.valueOf(dayOfMonth);
                        }
                        String startDateText = String.valueOf(year)+"-"+monthText+"-"+dayText+"T";
                        eventStartDayButton.setText(startDateText);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, onDateSetListener, y, m, d);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        eventEndDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthText;
                        String dayText;
                        if(month < 9) {
                            monthText = "0"+String.valueOf(month+1);
                        }
                        else {
                            monthText= String.valueOf(month+1);
                        }
                        if (dayOfMonth<10) {
                            dayText = "0"+String.valueOf(dayOfMonth);
                        }
                        else {
                            dayText = String.valueOf(dayOfMonth);
                        }
                        String endDateText = String.valueOf(year)+"-"+monthText+"-"+dayText+"T";
                        eventEndDayButton.setText(endDateText);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, onDateSetListener, y, m, d);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHANGE_LOC) {
            if (resultCode == Activity.RESULT_OK) {
                double[] coords =  {data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0)};
                getEventViewModel.setEventCoordinates(coords);
            }
        }
    }
}