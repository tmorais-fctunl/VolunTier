package pt.unl.fct.di.aldeia.apdc2021.data;

public class ValidateTokenRepository {

    private static volatile ValidateTokenRepository instance;

    private ValidateTokenDataSource dataSource;

    // private constructor : singleton access
    private ValidateTokenRepository(ValidateTokenDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ValidateTokenRepository getInstance(ValidateTokenDataSource dataSource) {
        if (instance == null) {
            instance = new ValidateTokenRepository(dataSource);
        }
        return instance;
    }


    public Result<Void> validate(String username, String token) {
        return dataSource.validate(username, token);
    }
}

