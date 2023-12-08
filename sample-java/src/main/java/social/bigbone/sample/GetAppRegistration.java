package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Scope;
import social.bigbone.api.entity.Application;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetAppRegistration {
    public static void main(final String[] args) throws BigBoneRequestException {
        final MastodonClient client = new MastodonClient.Builder("mstdn.jp").build();

        final Application application = client.apps().createApp(
                "bigbone-sample-app",
                "urn:ietf:wg:oauth:2.0:oob",
                "",
                new Scope(Scope.Name.READ, Scope.Name.WRITE, Scope.Name.PUSH)
        ).execute();
        System.out.println("client_id=" + application.getClientId());
        System.out.println("client_secret=" + application.getClientSecret());
    }
}
