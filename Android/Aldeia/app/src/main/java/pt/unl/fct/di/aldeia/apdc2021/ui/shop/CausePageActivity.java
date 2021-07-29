package pt.unl.fct.di.aldeia.apdc2021.ui.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModelFactory;

public class CausePageActivity extends AppCompatActivity {

    private MainLoggedInViewModel viewModel;
    private UserLocalStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this, new MainLoggedInViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(MainLoggedInViewModel.class);
        setContentView(R.layout.activity_cause_page);

        final ProgressBar loading = findViewById(R.id.causeProgress);
        final TextView causeName = findViewById(R.id.causeName);
        final TextView causeCompany = findViewById(R.id.causeCompany);
        final TextView causeWebsite = findViewById(R.id.causeWebsite);
        final TextView causeDescription = findViewById(R.id.causeDescription);
        final TextView causeRaised = findViewById(R.id.causeReached);
        final TextView causeGoal = findViewById(R.id.causeGoal);
        final Button causeCancel = findViewById(R.id.causeLeave);
        final Button causeDonate = findViewById(R.id.causeDonate);
        final EditText causeAmount = findViewById(R.id.causeAmount);
        storage= new UserLocalStore(this);
        Bundle extras = getIntent().getExtras();
        loading.setVisibility(View.INVISIBLE);
        causeName.setText(extras.getString("name"));
        causeCompany.setText(extras.getString("company"));
        causeDescription.setText(extras.getString("description"));
        causeRaised.setText(extras.getString("raised"));
        causeGoal.setText(extras.getString("goal"));
        causeWebsite.setText(extras.getString("website"));

        causeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        causeDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amount = 0;
                if(!causeAmount.getText().toString().equals("")) {
                    amount = Float.parseFloat(causeAmount.getText().toString());
                }
                if(amount > 0) {
                    viewModel.donate(extras.getString("email"), extras.getString("tokenID"),
                            extras.getString("cause_id"), amount);
                    loading.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplication(), R.string.shop_donation_error, Toast.LENGTH_LONG).show();
                }
            }
        });


        viewModel.getDonationResult().observe(this, new Observer<DonationResult>() {
            @Override
            public void onChanged(DonationResult donationResult) {
                loading.setVisibility(View.INVISIBLE);
                if (donationResult == null) {
                    return;
                }
                if (donationResult.getError() != null) {
                    Toast.makeText(getApplicationContext(), "Donation Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (donationResult.getSuccess() != null) {
                    Toast.makeText(getApplicationContext(), "Donation Successful! Thank You!", Toast.LENGTH_SHORT).show();
                    float decrease = Float.parseFloat(causeAmount.getText().toString());
                    float newVal = Float.parseFloat(causeRaised.getText().toString()) + decrease;
                    causeRaised.setText(String.valueOf(newVal));
                    storage.decreaseCoins(decrease);
                }
            }
        });

    }
}