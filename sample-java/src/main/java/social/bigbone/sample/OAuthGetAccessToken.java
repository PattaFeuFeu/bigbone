package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.MastodonRequest;
import social.bigbone.api.Scope;
import social.bigbone.api.entity.Token;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.Scanner;

public class OAuthGetAccessToken {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instanceName = args[0];
        final String clientId = args[1];
        final String clientSecret = args[2];
        final String redirectUri = args[3];

        final MastodonClient client = new MastodonClient.Builder(instanceName).build();
        final Scope fullScope = new Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL);
        final String state = "example_state";

        // example values generated via: https://example-app.com/pkce,
        // should be generated according to: https://oauth.net/2/pkce/
        final String codeVerifier = "4d165eefac426b3e61ef652b7b44d63aad113b0540ad25f5f10bc935";
        final String codeChallenge = "LYcAAS1MRj9aDDd1cgPxku0YgFtJFH6_3_RGCWZGvOc";

        final String url = client.oauth().getOAuthUrl(
                clientId,
                redirectUri,
                fullScope,
                state,
                codeChallenge,
                "S256" // currently no other method supported
        );
        System.out.println("Open authorization page and copy code:");
        System.out.println(url);
        System.out.println("Paste code:");
        String authCode;
        try (Scanner s = new Scanner(System.in)) {
            authCode = s.nextLine();
        }
        final MastodonRequest<Token> token = client.oauth().getUserAccessTokenWithAuthorizationCodeGrant(
                clientId,
                clientSecret,
                redirectUri,
                authCode,
                codeVerifier,
                fullScope);

        System.out.println("Access Token:");
        System.out.println(token.execute().getAccessToken());
    }
}
