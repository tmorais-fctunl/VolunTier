package pt.unl.fct.di.aldeia.apdc2021;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.CommunityProfileActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard.LeaderboardAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.AllCausesResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.CausePageActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.ShopAdapter;


public class ShopFragment extends Fragment implements OnLookUpItemClickListener {

    private final int DONATION_CODE = 98;
    private RecyclerView recyclerView;
    private MainLoggedInViewModel viewModel;
    private ShopFragment mfragment;
    private AllCausesData causes;
    private UserLocalStore storage;
    private TextView yourCoins;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfragment = this;
        storage= new UserLocalStore(getContext());
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        recyclerView = view.findViewById(R.id.communityRecyclerView);

        yourCoins = view.findViewById(R.id.shopCoins);

        UserFullData user = storage.getUserFullData();
        viewModel.setUserFullData(user);
        yourCoins.setText(String.valueOf(user.getCurrentCurrency()));
        final ProgressBar loading = view.findViewById(R.id.shop_loading);

        viewModel.getAllCauses(user.getEmail(), viewModel.getUserAuth().getTokenID());

        viewModel.getAllCausesResult().observe(getActivity(), new Observer<AllCausesResult>() {
            @Override
            public void onChanged(AllCausesResult allCausesResult) {
                loading.setVisibility(View.INVISIBLE);
                if (allCausesResult == null) {
                    return;
                }
                if (allCausesResult.getError() != null) {
                    Toast.makeText(getActivity(), "Causes Retrieval Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (allCausesResult.getSuccess() != null) {
                    causes= allCausesResult.getSuccess();
                    ShopAdapter adapter = new ShopAdapter(mfragment, getContext(), causes.getCauses());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DONATION_CODE) {
            viewModel.setUserFullData(storage.getUserFullData());
            yourCoins.setText(String.valueOf(storage.getUserFullData().getCurrentCurrency()));
        }
    }

    @Override
    public void onLookUpItemClickListener(int position) {
        CausesData data = causes.getCauses().get(position);
        Intent intent = new Intent(getActivity(), CausePageActivity.class);
        intent.putExtra("name", data.getName());
        intent.putExtra("company", data.getCompany_name());
        intent.putExtra("description", data.getDescription());
        intent.putExtra("raised", String.valueOf(data.getRaised()));
        intent.putExtra("goal", String.valueOf(data.getGoal()));
        intent.putExtra("website", data.getWebsite());
        intent.putExtra("cause_id", data.getId());
        intent.putExtra("email", viewModel.getUserAuth().getEmail());
        intent.putExtra("tokenID", viewModel.getUserAuth().getTokenID());
        startActivityForResult(intent, DONATION_CODE);
    }
}