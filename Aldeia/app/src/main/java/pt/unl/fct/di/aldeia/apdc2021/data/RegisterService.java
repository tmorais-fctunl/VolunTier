package pt.unl.fct.di.aldeia.apdc2021.data;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserRegistration;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterService {
    @POST("rest/register")
    Call<Void> registerUser(@Body UserRegistration newUser);
}
