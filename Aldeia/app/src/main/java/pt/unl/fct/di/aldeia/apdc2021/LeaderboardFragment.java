package pt.unl.fct.di.aldeia.apdc2021;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.YourData;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard.LeaderboardAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard.RankRequestResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;

public class LeaderboardFragment extends Fragment implements OnLookUpItemClickListener {

    private RecyclerView recyclerView;
    private MainLoggedInViewModel viewModel;
    private RankingReceivedData ranks;
    private LeaderboardFragment mfragment;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfragment = this;
        context=getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recyclerView = view.findViewById(R.id.leaderboardRecyclerView);
        viewModel.setRankRequestResult();
        final Switch mode = view.findViewById(R.id.leaderboardSwitch);
        final TextView yourScore = view.findViewById(R.id.leaderboardYourPoints);
        final TextView yourUser = view.findViewById(R.id.leaderboardYourUsername);
        final TextView yourName = view.findViewById(R.id.leaderboardYourFullName);
        final TextView yourEmail = view.findViewById(R.id.leaderboardYourEmail);
        final TextView yourRank = view.findViewById(R.id.leaderboardYourRank);
        final ImageView yourImage = view.findViewById(R.id.leaderboardYourImage);
        final Button loadMore = view.findViewById(R.id.leaderboard_load_more);
        final ProgressBar loading = view.findViewById(R.id.leaderboardProgressBar);
        UserAuthenticated user = viewModel.getUserAuth();

        viewModel.getRankingEvent(user.getTokenID(), user.getEmail(), null);
        loading.setVisibility(View.VISIBLE);
        viewModel.getRankRequestResult().observe(getActivity(), new Observer<RankRequestResult>() {
            @Override
            public void onChanged(RankRequestResult rankRequestResult) {
                loading.setVisibility(View.INVISIBLE);
                if (rankRequestResult == null) {
                    return;
                }
                if (rankRequestResult.getError() != null) {
                    Toast.makeText(getActivity(), "Ranking Retrieval Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (rankRequestResult.getSuccess() != null) {
                    loadMore.setVisibility(View.VISIBLE);
                    RankingReceivedData data = rankRequestResult.getSuccess();
                    if(ranks == null) {
                        ranks = data;
                        YourData curUser = ranks.getCurrent_user();
                        yourScore.setText(String.valueOf(curUser.getScore()));
                        yourUser.setText(curUser.getUsername());
                        yourName.setText(curUser.getFull_name());
                        yourEmail.setText(curUser.getEmail());
                        yourRank.setText(String.valueOf(curUser.getRank()));
                        String encodedImage = curUser.getPic_64();
                        if(encodedImage != null) {
                            byte[] decodedString = Base64.decode(encodedImage.substring(21), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            yourImage.setImageBitmap(decodedByte);
                        }
                        else {
                            yourImage.setImageResource(R.drawable.ic_profile);
                        }
                    }
                    else {
                        List<RankingData> oldRanks = ranks.getRanking();
                        List<RankingData> newRanks = data.getRanking();
                        for(int i = 0; i< newRanks.size(); i++) {
                            oldRanks.add(newRanks.get(i));
                        }
                        ranks = new RankingReceivedData(rankRequestResult.getSuccess().getResults(), oldRanks,rankRequestResult.getSuccess().getCursor(), null);
                    }
                    LeaderboardAdapter adapter = new LeaderboardAdapter(mfragment, getContext(), ranks.getRanking());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ranks.getResults().equals("MORE_RESULTS_AFTER_LIMIT")) {
                    if(mode.isChecked()) {
                        viewModel.getRankingPoints(user.getTokenID(), user.getEmail(), ranks.getCursor());
                    }
                    else {
                        viewModel.getRankingEvent(user.getTokenID(), user.getEmail(), ranks.getCursor());
                    }
                }
                else {
                    Toast.makeText(getActivity(), R.string.leaderboard_no_more,Toast.LENGTH_SHORT).show();
                }
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                ranks = null;
                if(mode.isChecked()) {
                    viewModel.getRankingPoints(user.getTokenID(), user.getEmail(), null);
                }
                else {
                    viewModel.getRankingEvent(user.getTokenID(), user.getEmail(), null);
                }
            }
        });


        return view;
    }

    @Override
    public void onLookUpItemClickListener(int position) {
        viewModel.setUserToLookUp(ranks.getRanking().get(position).getEmail());
        viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Look up"));
    }
}