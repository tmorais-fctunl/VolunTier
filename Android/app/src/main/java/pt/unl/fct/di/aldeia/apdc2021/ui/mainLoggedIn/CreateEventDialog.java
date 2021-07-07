package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import pt.unl.fct.di.aldeia.apdc2021.R;

public class CreateEventDialog extends AppCompatDialogFragment {

    private EventDialogListener listener;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_event_dialog, null);
        final TextView eventName = view.findViewById(R.id.event_eventName);
        final TextView startDate = view.findViewById(R.id.event_Date1);
        final TextView endDate = view.findViewById(R.id.event_Date2);

        builder.setView(view).setTitle(R.string.maps_createEvent).
                setNegativeButton(R.string.maps_event_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton(R.string.maps_event_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String event = eventName.getText().toString();
                String start = startDate.getText().toString();
                String end = endDate.getText().toString();
                listener.applyTexts(event, start, end);
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
        void applyTexts(String eventName, String startDate, String endDate);
        void dismissDialog();
    }
}
