package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Scope;
import social.bigbone.api.entity.Application;
import social.bigbone.api.entity.Token;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.OAuthMethods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

@SuppressWarnings("PMD.SystemPrintln")
final class Authenticator {
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    // Request all non-admin scopes during app registration and while getting the user access token,
    // because individual samples might require any or all of them. Generally, an app should request
    // the minimum necessary for its intended functionality.
    private static final Scope FULL_SCOPE = new Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL);

    private Authenticator() {
    }

    static MastodonClient appRegistrationIfNeeded(final String instanceName, final String credentialFilePath) throws IOException, BigBoneRequestException {
        final File file = new File(credentialFilePath);
        if (!file.exists()) {
            System.out.println("create $credentialFilePath.");
            file.createNewFile();
        }
        final Properties properties = new Properties();
        System.out.println("load $credentialFilePath.");
        properties.load(Files.newInputStream(file.toPath()));
        if (properties.get(CLIENT_ID) == null) {
            System.out.println("try app registration...");
            final Application application = application(instanceName);
            properties.put(CLIENT_ID, application.getClientId());
            properties.put(CLIENT_SECRET, application.getClientSecret());
            properties.store(Files.newOutputStream(file.toPath()), "app registration");
        } else {
            System.out.println("app registration found...");
        }
        final String clientId = properties.get(CLIENT_ID).toString();
        final String clientSecret = properties.get(CLIENT_SECRET).toString();
        if (properties.get(ACCESS_TOKEN) == null) {
            System.out.println("get access token for $instanceName...");
            System.out.println("please input your email...");
            final String email = System.console().readLine();
            System.out.println("please input your password...");
            final String pass = System.console().readLine();
            final Token accessToken = getAccessToken(instanceName, clientId, clientSecret, email, pass);
            properties.put(ACCESS_TOKEN, accessToken.getAccessToken());
            properties.store(Files.newOutputStream(file.toPath()), "app registration");
        } else {
            System.out.println("access token found...");
        }
        final MastodonClient.Builder builder = new MastodonClient.Builder(instanceName)
                .accessToken(properties.get(ACCESS_TOKEN).toString());
        return builder.build();
    }

    private static Token getAccessToken(final String instanceName, final String clientId, final String clientSecret, final String email, final String password) throws BigBoneRequestException {
        final MastodonClient client = new MastodonClient.Builder(instanceName).build();
        final OAuthMethods oauthMethods = new OAuthMethods(client);
        return oauthMethods.getUserAccessTokenWithPasswordGrant(clientId, clientSecret, REDIRECT_URI, email, password, FULL_SCOPE).execute();
    }

    private static Application application(final String instanceName) throws BigBoneRequestException {
        final MastodonClient client = new MastodonClient.Builder(instanceName).build();
        return client.apps().createApp("bigbone-sample-app", REDIRECT_URI, null, FULL_SCOPE).execute();
    }
}
