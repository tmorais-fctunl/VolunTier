package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.RemoveAccDialog;

public class CommentDialog extends DialogFragment {
    public interface CommentDialogListener {
        public void onDialogPositiveClick(String comment);
    }

    CommentDialog.CommentDialogListener listener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (CommentDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("This activity must implement CommentDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_comment_dialog, null);
        final EditText comment = view.findViewById(R.id.event_dialog_comment);
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
                if(comment.getText().toString().length()>500){
                    comment.setError("Too many characters...");
                }else{
                    comment.setError(null);
                }
            }
        };
        comment.addTextChangedListener(afterTextChangedListener);
        // Create the AlertDialog object and return it
        builder.setView(view).setMessage("Add Comment")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(comment.getError()==null){
                            listener.onDialogPositiveClick(comment.getText().toString());
                            dialog.dismiss();
                        }else{
                            //do nothing
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


}
