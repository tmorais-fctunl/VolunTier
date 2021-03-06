package pt.unl.fct.di.aldeia.apdc2021.data;

public class LogoutRepository {

    private static volatile LogoutRepository instance;

    private final LogoutDataSource dataSource;

    // private constructor : singleton access
    private LogoutRepository(LogoutDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LogoutRepository getInstance(LogoutDataSource dataSource) {
        if (instance == null) {
            instance = new LogoutRepository(dataSource);
        }
        return instance;
    }



}