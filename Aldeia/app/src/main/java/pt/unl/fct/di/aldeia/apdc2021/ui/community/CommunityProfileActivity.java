package pt.unl.fct.di.aldeia.apdc2021.ui.community;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModelFactory;

public class CommunityProfileActivity extends AppCompatActivity {

    private MainLoggedInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this, new MainLoggedInViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(MainLoggedInViewModel.class);
        setContentView(R.layout.community_profile);
        Bundle extras = getIntent().getExtras();
        String token = extras.getString("token");
        String email = email = extras.getString("email");
        String target = extras.getString("target");

        final ImageView comImage = findViewById(R.id.community_lookUp_ImageView);
        final TextView comUser = findViewById(R.id.community_lookUp_Username);
        final TextView comProf = findViewById(R.id.community_lookUp_PrivacyPublic);
        final TextView comFull = findViewById(R.id.community_lookUp_FullName);
        final TextView comLand = findViewById(R.id.community_lookUp_Landline);
        final TextView comMob = findViewById(R.id.community_lookUp_Mobile);
        final TextView comEmail = findViewById(R.id.community_lookUp_Email);
        final TextView comAddr = findViewById(R.id.community_lookUp_Address);
        final TextView comAddr2 = findViewById(R.id.community_lookUp_Address2);
        final TextView comRegion = findViewById(R.id.community_lookUp_Region);
        final TextView comZip = findViewById(R.id.community_lookUp_Pc);
        final TextView comWeb = findViewById(R.id.community_lookUp_Website);
        final TextView comFac = findViewById(R.id.community_lookUp_Facebook);
        final TextView comIns = findViewById(R.id.community_lookUp_Instagram);
        final TextView comTwi = findViewById(R.id.community_lookUp_Twitter);
        final Button comCancel = findViewById(R.id.community_lookUp_cancel);
        final ProgressBar comBar = findViewById(R.id.com_prof_load_bar);

        viewModel.lookUpCommunity(email, token, target);

        viewModel.getLookUpResultCommunity().observe(this, new Observer<LookUpResultCommunity>() {
            @Override
            public void onChanged(LookUpResultCommunity lookUpResult) {
                if(lookUpResult == null) {
                    return;
                }
                if (lookUpResult.getError() != null) {
                    return;
                }
                UserFullData data = lookUpResult.getSuccess();
                if (lookUpResult.getSuccess() != null) {
                    comBar.setVisibility(View.INVISIBLE);
                    comProf.setVisibility(View.VISIBLE);
                    comProf.setText(data.getProfile());
                    comUser.setVisibility(View.VISIBLE);
                    comUser.setText(data.getUsername());
                    comEmail.setVisibility(View.VISIBLE);
                    comEmail.setText(data.getEmail());
                    String encodedImage = data.getPic();
                    if(encodedImage != null) {
                        byte[] decodedString = Base64.decode(encodedImage.substring(21), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        comImage.setImageBitmap(decodedByte);
                    }
                    else {
                        comImage.setImageResource(R.drawable.ic_profile);
                    }
                    comImage.setVisibility(View.VISIBLE);
                    if(data.getProfile().equals("PUBLIC")) {
                        comFull.setVisibility(View.VISIBLE);
                        comFull.setText(data.getFullName());
                        comLand.setVisibility(View.VISIBLE);
                        comLand.setText(data.getLandline());
                        comMob.setVisibility(View.VISIBLE);
                        comMob.setText(data.getMobile());
                        comAddr.setVisibility(View.VISIBLE);
                        comAddr.setText(data.getAddress());
                        comAddr2.setVisibility(View.VISIBLE);
                        comAddr2.setText(data.getAddress2());
                        comRegion.setVisibility(View.VISIBLE);
                        comRegion.setText(data.getRegion());
                        comZip.setVisibility(View.VISIBLE);
                        comZip.setText(data.getPc());
                        comWeb.setVisibility(View.VISIBLE);
                        comWeb.setText(data.getWebsite());
                        comFac.setVisibility(View.VISIBLE);
                        comFac.setText(data.getFacebook());
                        comTwi.setVisibility(View.VISIBLE);
                        comTwi.setText(data.getTwitter());
                        comIns.setVisibility(View.VISIBLE);
                        comIns.setText(data.getInstagram());
                    }
                }
            }
        });

        comCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}