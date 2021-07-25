package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;

public class SearchEventCategoryRepository {

    private static volatile SearchEventCategoryRepository instance;

    private final SearchEventCategoryDataSource dataSource;

    // private constructor : singleton access
    private SearchEventCategoryRepository(SearchEventCategoryDataSource dataSource) {

        this.dataSource = dataSource;

    }

    public static SearchEventCategoryRepository getInstance(SearchEventCategoryDataSource dataSource) {
        if (instance == null) {
            instance = new SearchEventCategoryRepository(dataSource);
        }
        return instance;
    }


    public Result<SearchEventsCategoryReply> searchEventsCategory(String email, String token, String cursor, String category) {
        return dataSource.searchEventCategory(new RankingRequestData(email, token, cursor), category);
    }
}