package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class LoadingScreenRepository {
    private static volatile LoadingScreenRepository instance;

    private final LookUpDataSource dataSourceLookUp;



    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private LoadingScreenRepository(LookUpDataSource dataSourceLookUp) {
        this.dataSourceLookUp= dataSourceLookUp;

    }

    public static LoadingScreenRepository getInstance(LookUpDataSource dataSourceLookUp) {
        if (instance == null) {
            instance = new LoadingScreenRepository(dataSourceLookUp);
        }
        return instance;
    }



    public Result<UserFullData> lookUp(String email, String token, String target) {
        return dataSourceLookUp.lookUp(email, token, target);
    }

}
