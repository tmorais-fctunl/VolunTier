package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReply;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchRoutesDataSource {
    private final SearchRoutesService service;

    public SearchRoutesDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(SearchRoutesService.class);
    }

    public Result<SearchRoutesReply> searchRoutes(SearchEventsData data) {
        Call<SearchRoutesReply> searchEventsCall =service.searchRoutes(data) ;
        try {
            Response<SearchRoutesReply> response = searchEventsCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting routes", e));
        }
    }
}
