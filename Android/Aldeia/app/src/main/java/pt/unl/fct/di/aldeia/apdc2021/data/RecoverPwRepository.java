package pt.unl.fct.di.aldeia.apdc2021.data;

public class RecoverPwRepository {

    private static volatile RecoverPwRepository instance;

    private final RecoverPwDataSource dataSource;

    // private constructor : singleton access
    private RecoverPwRepository(RecoverPwDataSource dataSource) {
        this.dataSource = dataSource;

    }

    public static RecoverPwRepository getInstance(RecoverPwDataSource dataSource) {
        if (instance == null) {
            instance = new RecoverPwRepository(dataSource);
        }
        return instance;
    }


    public Result<Void> recoverPassword(String email) {
        return dataSource.recoverPassword(email);
    }
}