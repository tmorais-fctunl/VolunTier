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
import android.widget.EditText;
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

public class CreateRouteDialog extends AppCompatDialogFragment {

    private RouteDialogListener listener;
    private MainLoggedInViewModel viewModel;
    private int hour, min;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_route_dialog, null);
        final EditText routeNameEditText =view.findViewById(R.id.route_dialog_name);
        final EditText routeDescriptionEditText=view.findViewById(R.id.route_dialog_description);



        builder.setView(view).setTitle(R.string.add_route_dialog).
                setNegativeButton(R.string.maps_event_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.maps_event_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.createRoute(routeNameEditText.getText().toString(),routeDescriptionEditText.getText().toString());
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
                if(routeNameEditText.getText().toString().length()>0&& routeNameEditText.getText().toString().length()<100){
                    routeNameEditText.setError(null);
                }else{
                    routeNameEditText.setError("Not a valid name format");
                }
                if(routeDescriptionEditText.getText().toString().length()>0&& routeDescriptionEditText.getText().toString().length()<100){
                    routeDescriptionEditText.setError(null);
                }else{
                    routeDescriptionEditText.setError("Not a valid description format");
                }
            }

        };
        routeNameEditText.addTextChangedListener(afterTextChangedListener);
        routeDescriptionEditText.addTextChangedListener(afterTextChangedListener);

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (RouteDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("This activity must implement RouteDialogListener");
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface RouteDialogListener {
        void createRoute(String route_name,String description);
    }
}
