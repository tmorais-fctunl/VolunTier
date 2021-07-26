package pt.unl.fct.di.aldeia.apdc2021;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.EventProfileAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetEventActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.SpecialOnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.UpdateProfileDataFormState;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.CausePageActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.ShopAdapter;

public class ProfileFragment extends Fragment implements SpecialOnLookUpItemClickListener {

    private MainLoggedInViewModel viewModel;
    private RecyclerView recyclerViewMine;
    private RecyclerView recyclerViewJoined;
    private ProfileFragment mfragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfragment = this;
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_profile, container, false);

        final Button changePWButton= view.findViewById(R.id.profileChangePassword);
        final EditText profileEmailEditText = view.findViewById(R.id.profileEmail);
        final EditText profileUsernameEditText = view.findViewById(R.id.profileUsername);
        final EditText profileFullNameEditText = view.findViewById(R.id.profileFullName);
        final EditText profileAddressEditText = view.findViewById(R.id.profileAddress);
        final EditText profileAddress2EditText = view.findViewById(R.id.profileAddress2);
        final EditText profileCellphoneEditText = view.findViewById(R.id.profileMobile);
        final EditText profileLandlineEditText = view.findViewById(R.id.profileLandline);
        final EditText profileZipCodeEditText = view.findViewById(R.id.profilePc);
        final EditText profileTwitterEditText = view.findViewById(R.id.profileTwitter);
        final EditText profileInstagramEditText = view.findViewById(R.id.profileInstagram);
        final EditText profileFacebookEditText = view.findViewById(R.id.profileFacebook);
        final EditText profileWebsiteEditText = view.findViewById(R.id.profileWebsite);
        final EditText profileRegionEditText = view.findViewById(R.id.profileRegion);
        final Switch profilePrivacy = view.findViewById(R.id.profilePrivacyPublic);
        final ImageView profileImage = view.findViewById(R.id.profileImageView);
        UserFullData userFullData = viewModel.getUserFullData();
        UserAuthenticated userAuth = viewModel.getUserAuth();
        profileEmailEditText.setText(userFullData.getEmail());
        profileUsernameEditText.setText(userFullData.getUsername());
        profileFullNameEditText.setText(userFullData.getFullName());
        profileAddressEditText.setText(userFullData.getAddress());
        profileAddress2EditText.setText(userFullData.getAddress2());
        profileCellphoneEditText.setText(userFullData.getMobile());
        profileLandlineEditText.setText(userFullData.getLandline());
        profileZipCodeEditText.setText(userFullData.getPc());
        profileTwitterEditText.setText(userFullData.getTwitter());
        profileInstagramEditText.setText(userFullData.getInstagram());
        profileFacebookEditText.setText(userFullData.getFacebook());
        profileWebsiteEditText.setText(userFullData.getWebsite());
        profileRegionEditText.setText(userFullData.getRegion());

        if(userFullData.getEvents_created() != null) {
            recyclerViewMine = view.findViewById(R.id.profile_recycler_created);
            EventProfileAdapter adapterMine = new EventProfileAdapter(mfragment, getContext(), userFullData.getEvents_created());
            recyclerViewMine.setAdapter(adapterMine);
            recyclerViewMine.setLayoutManager(new LinearLayoutManager(getContext()));

        }
        if(userFullData.getEvents_participating() != null) {
            recyclerViewJoined = view.findViewById(R.id.profile_recycler_joined);
            EventProfileAdapter adapterJoined = new EventProfileAdapter(mfragment, getContext(), userFullData.getEvents_participating());
            recyclerViewJoined.setAdapter(adapterJoined);
            recyclerViewJoined.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        switch (viewModel.getImagePath()){
            case"":

                break;
            default:
                try {
                    File f=new File(viewModel.getImagePath(), "profile.jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                    profileImage.setImageBitmap(b);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                break;
        }
        switch(userFullData.getProfile().toUpperCase()){
            case "PRIVATE":
                profilePrivacy.setChecked(true);
                break;
            default:
                profilePrivacy.setChecked(false);
                break;
        }

        final Button logoutButton = view.findViewById(R.id.profileLogout);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Logout"));
            }
        });

        final Button saveChangesButton = view.findViewById(R.id.profileChanges);
        viewModel.getProfileFormState().observe(this.getViewLifecycleOwner(), new Observer<UpdateProfileDataFormState>() {
            @Override
            public void onChanged(@Nullable UpdateProfileDataFormState profileFormState) {
                if (profileFormState == null) {
                    return;
                }
                saveChangesButton.setEnabled(profileFormState.isDataValid());
                if (!profileZipCodeEditText.getText().toString().equals("") && profileFormState.getPostalCodeError() != null) {
                    profileZipCodeEditText.setError(getString(profileFormState.getPostalCodeError()));
                }
                if (!profileCellphoneEditText.getText().toString().equals("") && profileFormState.getMobileError() != null) {
                    profileCellphoneEditText.setError(getString(profileFormState.getMobileError()));
                }
                if (!profileLandlineEditText.getText().toString().equals("") && profileFormState.getLandLineError() != null) {
                    profileLandlineEditText.setError(getString(profileFormState.getLandLineError()));
                }
                if (!profileAddressEditText.getText().toString().equals("") && profileFormState.getAddressError() != null) {
                    profileAddressEditText.setError(getString(profileFormState.getAddressError()));
                }
                if (!profileRegionEditText.getText().toString().equals("") && profileFormState.getRegionError() != null) {
                    profileRegionEditText.setError(getString(profileFormState.getRegionError()));
                }
                if (!profileFullNameEditText.getText().toString().equals("") && profileFormState.getFullNameError() != null) {
                    profileFullNameEditText.setError(getString(profileFormState.getFullNameError()));
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
                viewModel.updateProfileDataChanged(profileZipCodeEditText.getText().toString(),
                        profileCellphoneEditText.getText().toString(),profileLandlineEditText.getText().toString()
                        , profileAddressEditText.getText().toString(),profileRegionEditText.getText().toString(),
                        profileFullNameEditText.getText().toString());
            }
        };
        profileZipCodeEditText.addTextChangedListener(afterTextChangedListener);
        profileCellphoneEditText.addTextChangedListener(afterTextChangedListener);
        profileLandlineEditText.addTextChangedListener(afterTextChangedListener);
        profileAddressEditText.addTextChangedListener(afterTextChangedListener);
        profileRegionEditText.addTextChangedListener(afterTextChangedListener);
        profileFullNameEditText.addTextChangedListener(afterTextChangedListener);

        saveChangesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String privacy="";
                if(profilePrivacy.isChecked()){
                    privacy="PRIVATE";
                }else{
                    privacy="PUBLIC";
                }
                String fullName=profileFullNameEditText.getText().toString();
                String landline=profileLandlineEditText.getText().toString();
                String mobile =profileCellphoneEditText.getText().toString();
                String address=profileAddressEditText.getText().toString();
                String address2=profileAddress2EditText.getText().toString();
                String region=profileRegionEditText.getText().toString();
                String pc=profileZipCodeEditText.getText().toString();
                String website=profileWebsiteEditText.getText().toString();
                String facebook=profileFacebookEditText.getText().toString();
                String instagram=profileInstagramEditText.getText().toString();
                String twitter=profileTwitterEditText.getText().toString();
                viewModel.setUserUpdate(new UserUpdateData(userAuth.getEmail(),userAuth.getTokenID(), userAuth.getEmail(),fullName,privacy,
                        landline,mobile,address,address2,region,pc,website,facebook,instagram,twitter));
                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Update Data"));
            }
        });

        changePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Change Password"));
            }
        });

        final Button removeAccButton = view.findViewById(R.id.profileDeleteAccount);
        removeAccButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Remove"));
            }
        });
        final ImageButton editPhotoButton=view.findViewById(R.id.changeProfilePhoto);
        editPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Edit Photo"));
            }
        });

        return view;
    }

    @Override
    public void specialOnLookUpItemClickListener(String event) {
        Intent intent = new Intent(getActivity(), GetEventActivity.class);
        intent.putExtra("event_id", event);
        startActivity(intent);
    }

}