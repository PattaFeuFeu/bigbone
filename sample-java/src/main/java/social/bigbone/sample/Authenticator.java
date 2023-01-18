package social.bigbone.sample;

import com.google.gson.Gson;
import social.bigbone.MastodonClient;
import social.bigbone.MastodonRequest;
import social.bigbone.Parameters;
import social.bigbone.api.Scope;
import social.bigbone.api.entity.auth.AccessToken;
import social.bigbone.api.entity.auth.AppRegistration;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.AppsMethods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

@SuppressWarnings("PMD.SystemPrintln")
final class Authenticator {
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String ACCESS_TOKEN = "access_token";

    private Authenticator() {}

    static MastodonClient appRegistrationIfNeeded(final String instanceName, final String credentialFilePath, final boolean useStreaming) throws IOException, BigBoneRequestException {
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
            final AppRegistration appRegistration = appRegistration(instanceName);
            properties.put(CLIENT_ID, appRegistration.getClientId());
            properties.put(CLIENT_SECRET, appRegistration.getClientSecret());
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
            final AccessToken accessToken = getAccessToken(instanceName, clientId, clientSecret, email, pass);
            properties.put(ACCESS_TOKEN, accessToken.getAccessToken());
            properties.store(Files.newOutputStream(file.toPath()), "app registration");
        } else {
            System.out.println("access token found...");
        }
        final MastodonClient.Builder builder = new MastodonClient.Builder(instanceName)
            .accessToken(properties.get(ACCESS_TOKEN).toString());
        if (useStreaming) {
            builder.useStreamingApi();
        }
        return builder.build();
    }

    private static AccessToken getAccessToken(final String instanceName, final String clientId, final String clientSecret, final String email, final String password) throws BigBoneRequestException {
        final MastodonClient client = new MastodonClient.Builder(instanceName).build();
        return postUserNameAndPassword(client, clientId, clientSecret, new Scope(), email, password).execute();
    }

    private static AppRegistration appRegistration(final String instanceName) throws BigBoneRequestException {
        final MastodonClient client = new MastodonClient.Builder(instanceName).build();
        final AppsMethods appsMethods = new AppsMethods(client);
        return appsMethods.createApp("kotlindon", "urn:ietf:wg:oauth:2.0:oob", new Scope(), null).execute();
    }

    /**
     * Obtain an access token, to be used during API calls that are not public. This method uses a grant_type
     * that is undocumented in Mastodon API, and should NOT be used in production code. It will be removed at a later date.
     * Apps.getAccessToken() should be used instead.
     */
    private static MastodonRequest<AccessToken> postUserNameAndPassword(
            final MastodonClient client,
            final String clientId,
            final String clientSecret,
            final Scope scope,
            final String userName,
            final String password
            ) {
        final Parameters parameters = new Parameters()
                .append("client_id", clientId)
                .append("client_secret", clientSecret)
                .append("scope", scope.toString())
                .append("username", userName)
                .append("password", password)
                .append("grant_type", "password");
        return new MastodonRequest<>(
                () -> client.post("oauth/token", parameters),
                s -> new Gson().fromJson(s, AccessToken.class)
        );
    }
}
