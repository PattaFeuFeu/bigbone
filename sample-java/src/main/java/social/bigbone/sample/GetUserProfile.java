package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Profile;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetUserProfile {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.ACCOUNTS or Scope.PROFILE.ALL
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance).accessToken(accessToken).build();

        // Get and display user profile
        final Profile profile = client.profile().getUserProfile().execute();

        System.out.println(profile.getDisplayName());
        System.out.println(profile.getId());
        System.out.println(profile.getNote());
        for (Profile.Field field : profile.getFields()) {
            System.out.print(field.getName());
            System.out.print(": ");
            System.out.println(field.getValue());
        }
    }
}
