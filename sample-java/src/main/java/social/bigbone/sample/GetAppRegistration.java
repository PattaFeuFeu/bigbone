package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Scope;
import social.bigbone.api.entity.Application;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetAppRegistration {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];
        final MastodonClient client = new MastodonClient.Builder(instance).build();

        // Request all non-admin scopes during app registration, because individual samples might require
        // any or all of them. Generally, an app should request the minimum necessary for its intended functionality.
        final Scope fullScope = new Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL);

        final Application application = client.apps().createApp(
                "bigbone-sample-app",
                "urn:ietf:wg:oauth:2.0:oob",
                "",
                fullScope
        ).execute();
        System.out.println("client_id=" + application.getClientId());
        System.out.println("client_secret=" + application.getClientSecret());
    }
}
