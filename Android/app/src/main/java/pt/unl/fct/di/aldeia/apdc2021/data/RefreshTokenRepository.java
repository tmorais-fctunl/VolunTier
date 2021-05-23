package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class RefreshTokenRepository {
    private static volatile RefreshTokenRepository instance;

    private RefreshTokenDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore


    // private constructor : singleton access
    private RefreshTokenRepository(RefreshTokenDataSource dataSource) {

        this.dataSource = dataSource;

    }

    public static RefreshTokenRepository getInstance(RefreshTokenDataSource dataSource) {
        if (instance == null) {
            instance = new RefreshTokenRepository(dataSource);
        }
        return instance;
    }



    public Result<UserAuthenticated> refreshToken(String username, String refreshToken) {
        Result<UserAuthenticated> result = dataSource.refreshToken(username, refreshToken);
        return result;
    }
}
