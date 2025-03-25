package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Markers;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetMarkers {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.STATUSES
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Get markers
        final Markers markers = client.markers().getMarkers().execute();
        System.out.println(markers);
    }
}
