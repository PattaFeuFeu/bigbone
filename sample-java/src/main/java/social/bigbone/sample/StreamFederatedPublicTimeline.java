package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.Closeable;
import java.io.IOException;

public class StreamFederatedPublicTimeline {
    public static void main(final String[] args) throws BigBoneRequestException, InterruptedException, IOException {
        final String instance = args[0];

        // access token with at least Scope.READ.STATUSES
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Start federated timeline streaming and stop after 20 seconds
        try (Closeable ignored = client.streaming().federatedPublic(false, System.out::println)) {
            Thread.sleep(20_000L);
        }
    }
}
