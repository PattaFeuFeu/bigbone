package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetBookmarks {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.BOOKMARKS
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Get bookmarks
        final Pageable<Status> bookmarks = client.bookmarks().getBookmarks().execute();
        bookmarks.getPart().forEach(bookmark -> {
            String statusText = bookmark.getContent() + "\n";
            System.out.print(statusText);
        });
    }
}
