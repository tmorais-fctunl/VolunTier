package pt.unl.fct.di.aldeia.apdc2021.data;

public class RegisterRepository {

    private static volatile RegisterRepository instance;

    private final RegisterDataSource dataSource;

    // private constructor : singleton access
    private RegisterRepository(RegisterDataSource dataSource) {

        this.dataSource = dataSource;

    }

    public static RegisterRepository getInstance(RegisterDataSource dataSource) {
        if (instance == null) {
            instance = new RegisterRepository(dataSource);
        }
        return instance;
    }


    public Result<Void> register(String username, String email, String password) {
        return dataSource.register(username, email, password);
    }
}