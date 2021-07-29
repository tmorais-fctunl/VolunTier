package pt.unl.fct.di.aldeia.apdc2021.ui.event.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReplyUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetEventActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.SpecialOnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard.LeaderboardAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModelFactory;

public class EventCategoryActivity extends AppCompatActivity implements SpecialOnLookUpItemClickListener {

    private RecyclerView recyclerView;
    private EventCategoryViewModel viewModel;
    private EventCategoryActivity mActivity;
    private UserLocalStore storage;
    private EventCategoryAdapter eventAdapter;
    private Context context;
    private boolean addingEvents;
    private SearchEventsCategoryReply reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_category);
        mActivity = this;
        context = this;
        storage = new UserLocalStore(this);
        viewModel= new ViewModelProvider(mActivity, new EventCategoryViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(EventCategoryViewModel.class);
        recyclerView = findViewById(R.id.eventcategoryRecyclerView);

        final Spinner eventCategorySpinner = findViewById(R.id.eventcategory_spinner);
        final ProgressBar loading = findViewById(R.id.eventcategory_prog_bar);
        final TextView noEvents = findViewById(R.id.act_event_cat_noevents);
        eventAdapter = null;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategorySpinner.setAdapter(adapter);
        addingEvents = false;
        UserAuthenticated user = storage.getLoggedInUser();


        eventCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reply = null;
                noEvents.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                String category = eventCategorySpinner.getSelectedItem().toString();
                switch (category) {
                    case "Ajuda a Criancas":
                        category = "AJUDA_A_CRIANCAS";
                        break;
                    case "Ajuda a Idosos":
                        category = "AJUDA_A_IDOSOS";
                        break;
                    case "Ajuda a Sem Abrigo":
                        category = "AJUDA_A_SEM_ABRIGO";
                        break;
                    case "Ajuda Desportiva":
                        category = "AJUDA_DESPORTIVA";
                        break;
                    case "Ajuda Empresarial":
                        category = "AJUDA_EMPRESARIAL";
                        break;
                    case "Ajudar Portadores de Deficiencia":
                        category = "AJUDAR_PORTADORES_DE_DEFICIENCIA";
                        break;
                    case "Auxilio de Doentes":
                        category = "AUXILIO_DE_DOENTES";
                        break;
                    case "Comunicacao Digital":
                        category = "COMUNICACAO_DIGITAL";
                        break;
                    case "Construcao":
                        category = "CONSTRUCAO";
                        break;
                    case "Cuidar de Animais":
                        category = "CUIDAR_DE_ANIMAIS";
                        break;
                    case "Desastres Ambientais":
                        category = "DESASTRES_AMBIENTAIS";
                        break;
                    case "Ensinar Idiomas":
                        category = "ENSINAR_IDIOMAS";
                        break;
                    case "Ensinar Musica":
                        category = "ENSINAR_MUSICA";
                        break;
                    case "Iniciativas Ambientais":
                        category = "INICIATIVAS_AMBIENTAIS";
                        break;
                    case "Internacional":
                        category = "INTERNACIONAL";
                        break;
                    case "Promocao":
                        category = "PROMOCAO";
                        break;
                    case "Protecao Civil":
                        category = "PROTECAO_CIVIL";
                        break;
                    case "Reciclagem":
                        category = "RECICLAGEM";
                        break;
                    case "Social":
                        category = "SOCIAL";
                        break;
                    default:
                        break;
                }
                viewModel.getSearchEventCategory(user.getEmail(), user.getTokenID(), null, category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewModel.getSearchEventCategoryResult().observe(mActivity, new Observer<GetSearchEventCategoryResult>() {
            @Override
            public void onChanged(GetSearchEventCategoryResult getSearchEventCategoryResult) {
                loading.setVisibility(View.INVISIBLE);
                if (getSearchEventCategoryResult == null) {
                    return;
                }
                if (getSearchEventCategoryResult.getError() != null) {
                    Toast.makeText(mActivity, "Event Retrieval Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (getSearchEventCategoryResult.getSuccess() != null) {
                    SearchEventsCategoryReply data = getSearchEventCategoryResult.getSuccess();
                    if(reply == null) {
                        reply=data;
                        if(reply.getEvents().size() == 0) {
                            noEvents.setVisibility(View.VISIBLE);
                        }
                        eventAdapter = new EventCategoryAdapter(mActivity, context, reply.getEvents());
                        recyclerView.setAdapter(eventAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                    else {
                        List<SearchEventsReplyUnit> oldEvents = reply.getEvents();
                        List<SearchEventsReplyUnit> newEvents = data.getEvents();
                        for(int i = 0; i< newEvents.size(); i++) {
                            oldEvents.add(newEvents.get(i));
                        }
                        reply = new SearchEventsCategoryReply(oldEvents, data.getCursor(), data.getResults());
                        eventAdapter.notifyDataSetChanged();
                    }
                    addingEvents = false;
                }
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)) {
                    if(!addingEvents){
                        addingEvents=true;
                        if(reply.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                            String category = eventCategorySpinner.getSelectedItem().toString();
                            switch (category) {
                                case "Ajuda a Criancas":
                                    category = "AJUDA_A_CRIANCAS";
                                    break;
                                case "Ajuda a Idosos":
                                    category = "AJUDA_A_IDOSOS";
                                    break;
                                case "Ajuda a Sem Abrigo":
                                    category = "AJUDA_A_SEM_ABRIGO";
                                    break;
                                case "Ajuda Desportiva":
                                    category = "AJUDA_DESPORTIVA";
                                    break;
                                case "Ajuda Empresarial":
                                    category = "AJUDA_EMPRESARIAL";
                                    break;
                                case "Ajudar Portadores de Deficiencia":
                                    category = "AJUDAR_PORTADORES_DE_DEFICIENCIA";
                                    break;
                                case "Auxilio de Doentes":
                                    category = "AUXILIO_DE_DOENTES";
                                    break;
                                case "Comunicacao Digital":
                                    category = "COMUNICACAO_DIGITAL";
                                    break;
                                case "Construcao":
                                    category = "CONSTRUCAO";
                                    break;
                                case "Cuidar de Animais":
                                    category = "CUIDAR_DE_ANIMAIS";
                                    break;
                                case "Desastres Ambientais":
                                    category = "DESASTRES_AMBIENTAIS";
                                    break;
                                case "Ensinar Idiomas":
                                    category = "ENSINAR_IDIOMAS";
                                    break;
                                case "Ensinar Musica":
                                    category = "ENSINAR_MUSICA";
                                    break;
                                case "Iniciativas Ambientais":
                                    category = "INICIATIVAS_AMBIENTAIS";
                                    break;
                                case "Internacional":
                                    category = "INTERNACIONAL";
                                    break;
                                case "Promocao":
                                    category = "PROMOCAO";
                                    break;
                                case "Protecao Civil":
                                    category = "PROTECAO_CIVIL";
                                    break;
                                case "Reciclagem":
                                    category = "RECICLAGEM";
                                    break;
                                case "Social":
                                    category = "SOCIAL";
                                    break;
                                default:
                                    break;
                            }
                            viewModel.getSearchEventCategory(user.getEmail(), user.getTokenID(), reply.getCursor(), category);
                        }else{
                            Toast.makeText(mActivity, "That's all the events from this category",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void specialOnLookUpItemClickListener(String event) {
        Intent intent = new Intent(this, GetEventActivity.class);
        intent.putExtra("event_id", event);
        startActivity(intent);
    }
}