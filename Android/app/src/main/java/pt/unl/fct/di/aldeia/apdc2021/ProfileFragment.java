package pt.unl.fct.di.aldeia.apdc2021;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.UpdateProfileDataFormState;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainLoggedInViewModel viewModel;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
                viewModel.setUserUpdate(new UserUpdateData(userAuth.getEmail(),userAuth.getTokenID(),fullName,privacy,
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



        return view;
    }

}