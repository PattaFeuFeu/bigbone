package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Search;
import social.bigbone.api.exception.BigBoneRequestException;

public class PerformSimpleSearch {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.STATUSES (or none for some parameter combinations)
        final String accessToken = args[1];

        final String searchTerm = args[2];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Perform search and print results
        final Search searchResult = client.search().searchContent(searchTerm).execute();
        searchResult.getAccounts().forEach(account -> System.out.println(account.getDisplayName()));
        searchResult.getStatuses().forEach(status -> System.out.println(status.getContent()));
        searchResult.getHashtags().forEach(hashtag -> System.out.println(hashtag.getName()));
    }
}
