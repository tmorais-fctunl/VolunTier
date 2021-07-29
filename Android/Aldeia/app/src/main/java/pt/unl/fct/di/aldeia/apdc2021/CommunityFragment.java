package pt.unl.fct.di.aldeia.apdc2021;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.CommunityAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.SearchUserResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;

public class CommunityFragment extends Fragment implements OnLookUpItemClickListener {

    private RecyclerView comRecyclerView;
    private MainLoggedInViewModel viewModel;
    private CommunityFragment mfragment;
    private CommunityAdapter adapter;
    private SearchUserData users;
    private Context context;
    private boolean addingUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfragment=this;
        context = getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        comRecyclerView = view.findViewById(R.id.communityRecyclerView);
        viewModel.nukeSearchUserResult();
        addingUsers = false;
        final SearchView comSearch = view.findViewById(R.id.community_search_view);
        final ProgressBar comProg = view.findViewById(R.id.com_prog_bar);
        final TextView comNoUsers = view.findViewById(R.id.com_no_users);
        UserAuthenticated user = viewModel.getUserAuth();

        comSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                comNoUsers.setVisibility(View.INVISIBLE);
                comProg.setVisibility(View.VISIBLE);
                users = null;
                viewModel.getSearchUser(user.getTokenID(),user.getEmail(),null, String.valueOf(comSearch.getQuery()));
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                comNoUsers.setVisibility(View.INVISIBLE);
                users = null;
                return false;
            }
        });

        comSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comNoUsers.setVisibility(View.INVISIBLE);
                users = null;
                if(!comSearch.getQuery().equals("")) {
                    comProg.setVisibility(View.VISIBLE);
                    viewModel.getSearchUser(user.getTokenID(),user.getEmail(),null, String.valueOf(comSearch.getQuery()));
                }
            }
        });


        viewModel.getSearchUserResult().observe(getActivity(), new Observer<SearchUserResult>() {
            @Override
            public void onChanged(SearchUserResult searchUserResult) {
                comProg.setVisibility(View.INVISIBLE);
                if (searchUserResult == null) {
                    return;
                }
                if (searchUserResult.getError() != null) {
                    Toast.makeText(getActivity(), "User Retrieval Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (searchUserResult.getSuccess() != null) {
                    SearchUserData data = searchUserResult.getSuccess();
                    if(users == null) {
                        users = data;
                        if(users.getUsers().size() == 0) {
                            comNoUsers.setVisibility(View.VISIBLE);
                        }
                        adapter = new CommunityAdapter(mfragment,getContext(), users.getUsers());
                        comRecyclerView.setAdapter(adapter);
                        comRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                    else {
                        List<SearchData> oldUsers = users.getUsers();
                        List<SearchData> newUsers = data.getUsers();
                        for(int i = 0; i< newUsers.size(); i++) {
                            oldUsers.add(newUsers.get(i));
                        }
                        users = new SearchUserData(searchUserResult.getSuccess().getResults(), oldUsers,searchUserResult.getSuccess().getCursor());
                        adapter.notifyDataSetChanged();
                    }
                    addingUsers = false;
                }
            }
        });

        comRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)) {
                    if(!addingUsers){
                        addingUsers=true;
                        if(users.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                            viewModel.getSearchUser(user.getTokenID(),user.getEmail(), users.getCursor(), String.valueOf(comSearch.getQuery()));
                        }else{
                            Toast.makeText(getActivity(), R.string.community_no_more,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onLookUpItemClickListener(int position) {
        viewModel.setUserToLookUp(users.getUsers().get(position).getEmail());
        viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Look up"));
    }
}