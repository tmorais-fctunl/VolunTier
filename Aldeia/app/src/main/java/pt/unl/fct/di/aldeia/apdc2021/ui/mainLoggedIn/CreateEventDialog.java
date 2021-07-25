package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;

public class CreateEventDialog extends AppCompatDialogFragment {

    private EventDialogListener listener;
    private MainLoggedInViewModel viewModel;
    private int hour, min;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_event_dialog, null);
        viewModel = new ViewModelProvider(this, new MainLoggedInViewModelFactory(((App) getActivity().getApplication()).getExecutorService()))
                .get(MainLoggedInViewModel.class);
        final TextView eventName = view.findViewById(R.id.event_eventName);
        final TextView eventDescription = view.findViewById(R.id.event_eventDescription);
        final Spinner eventCategory = view.findViewById(R.id.event_eventCategorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategory.setAdapter(adapter);
        final Spinner eventDifficulty = view.findViewById(R.id.event_difficultySpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.difficulties));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventDifficulty.setAdapter(adapter2);
        final Button startDate = view.findViewById(R.id.event_startDate);
        final Button endDate = view.findViewById(R.id.event_endDate);
        final Button startTime = view.findViewById(R.id.event_startTime);
        final Button endTime = view.findViewById(R.id.event_endTime);
        final TextView eventContact = view.findViewById(R.id.event_dialog_comment);
        final TextView eventCapacity = view.findViewById(R.id.event_eventCapacity);
        final Switch eventProfile = view.findViewById(R.id.event_eventProfile);

        startTime.setOnClickListener(new View.OnClickListener() {
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
                        startTime.setText(startTimeText);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener,hour, min, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
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
                        endTime.setText(endTimeText);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener,hour, min, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
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
                        startDate.setText(startDateText);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, y, m, d);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
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
                        endDate.setText(endDateText);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, y, m, d);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        viewModel.getCreateEventFormState().observe(this, new Observer<CreateEventFormState>() {
            @Override
            public void onChanged(CreateEventFormState createEventFormState) {
                if (createEventFormState == null) {
                    return;
                }
                //TODO botao mudar
                if (createEventFormState.getNameError() != null) {
                    eventName.setError(getString(createEventFormState.getNameError()));
                }
                if (createEventFormState.getDateError() != null) {
                    startDate.setError(getString(createEventFormState.getDateError()));
                    startTime.setError(getString(createEventFormState.getDateError()));
                    endDate.setError(getString(createEventFormState.getDateError()));
                    endTime.setError(getString(createEventFormState.getDateError()));
                }else{
                    startDate.setError(null);
                    startTime.setError(null);
                    endDate.setError(null);
                    endTime.setError(null);
                }
                if (createEventFormState.getDescriptionError() != null) {
                    eventDescription.setError(getString(createEventFormState.getDescriptionError()));
                }
                if (!eventContact.getText().toString().equals("") && createEventFormState.getContactError() != null) {
                    eventContact.setError(getString(createEventFormState.getContactError()));
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
                viewModel.createEventDataChanged(eventName.getText().toString(),
                        startDate.getText().toString() + startTime.getText().toString(),
                        endDate.getText().toString() + endTime.getText().toString(),
                        eventDescription.getText().toString(), eventContact.getText().toString());
            }
        };
        eventName.addTextChangedListener(afterTextChangedListener);
        startDate.addTextChangedListener(afterTextChangedListener);
        startTime.addTextChangedListener(afterTextChangedListener);
        endDate.addTextChangedListener(afterTextChangedListener);
        endTime.addTextChangedListener(afterTextChangedListener);
        eventDescription.addTextChangedListener(afterTextChangedListener);
        eventContact.addTextChangedListener(afterTextChangedListener);

        builder.setView(view).setTitle(R.string.maps_createEvent).
                setNegativeButton(R.string.maps_event_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.maps_event_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String event = eventName.getText().toString();
                String start = startDate.getText().toString() + startTime.getText().toString();
                String end = endDate.getText().toString() + endTime.getText().toString();
                String description = eventDescription.getText().toString();
                String category = eventCategory.getSelectedItem().toString();
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
                String profile = "PUBLIC";
                if(eventProfile.isChecked()) {
                    profile = "PRIVATE";
                }
                String contact = eventContact.getText().toString();
                int capacity = 10;
                if(!eventCapacity.getText().toString().equals("")) {
                    capacity = Integer.parseInt(eventCapacity.getText().toString());
                }
                int difficulty= Integer.parseInt(eventDifficulty.getSelectedItem().toString());
                listener.applyTexts(event, start, end, description, category, profile, contact, capacity,difficulty);
            }
        });
        return builder.create();
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (EventDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("This activity must implement EventDialogListener");
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.dismissDialog();
    }

    public interface EventDialogListener {
        void applyTexts(String eventName, String startDate, String endDate, String description, String category, String profile, String contact, int capacity,int difficulty);
        void dismissDialog();
    }
}
