package pt.unl.fct.di.aldeia.apdc2021.data;

public class ChangePasswordRepository {
    private static volatile ChangePasswordRepository instance;

    private final ChangePasswordDataSource dataSource;

    // private constructor : singleton access
    private ChangePasswordRepository(ChangePasswordDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ChangePasswordRepository getInstance(ChangePasswordDataSource dataSource) {
        if (instance == null) {
            instance = new ChangePasswordRepository(dataSource);
        }
        return instance;
    }
    public Result<Void> changePassword(String email, String token,String target, String oldPassword,String password,String passwordConfirmation) {
        return dataSource.changeProfilePassword(email, token,target,oldPassword,password,passwordConfirmation);
    }

}
