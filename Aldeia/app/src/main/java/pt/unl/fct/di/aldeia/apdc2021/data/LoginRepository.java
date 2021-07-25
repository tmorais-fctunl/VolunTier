package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LookUpDataSource dataSourceLookUp;

    private final LoginDataSource dataSourceLogin;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSourceLogin, LookUpDataSource dataSourceLookUp) {

        this.dataSourceLogin = dataSourceLogin;
        this.dataSourceLookUp= dataSourceLookUp;

    }

    public static LoginRepository getInstance(LoginDataSource dataSourceLogin ,LookUpDataSource dataSourceLookUp) {
        if (instance == null) {
            instance = new LoginRepository(dataSourceLogin,dataSourceLookUp);
        }
        return instance;
    }


    public Result<UserAuthenticated> login(String email, String password) {
        // handle login
        Result<UserAuthenticated> result = dataSourceLogin.login(email, password);
        return result;
    }

    public Result<UserFullData> lookUp(String email, String token, String target) {
        return dataSourceLookUp.lookUp(email, token, target);
    }

}