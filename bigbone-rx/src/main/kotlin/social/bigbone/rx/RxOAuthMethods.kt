package social.bigbone.rx

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Scope
import social.bigbone.api.entity.Token
import social.bigbone.api.method.OAuthMethods

/**
 * Reactive implementation of [OAuthMethods].
 * Allows access to API methods with endpoints having an "oauth" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/oauth/">Mastodon oauth API methods</a>
 */
class RxOAuthMethods(client: MastodonClient) {

    private val oAuthMethods = OAuthMethods(client)

    /**
     * Returns a URL that can be used to display an authorization form to the user. If approved,
     * it will create and return an authorization code, then redirect to the desired redirectUri,
     * or show the authorization code if redirectUri is "urn:ietf:wg:oauth:2.0:oob".
     * The authorization code can be used while requesting a token to obtain access to user-level methods.
     * @param clientId The client ID, obtained during app registration.
     * @param redirectUri Set a URI to redirect the user to. Must match one of the redirect_uris declared during app registration.
     * @param scope List of requested OAuth scopes, separated by spaces. Must be a subset of scopes declared during app registration.
     * @param state Arbitrary value to pass through to your server when the user authorizes or rejects the authorization request.
     * @param codeChallenge The PKCE code challenge for the authorization request.
     * @param codeChallengeMethod Must currently be `S256`, as this is the only code challenge method that is supported by Mastodon for PKCE.
     * @param forceLogin Forces the user to re-login, which is necessary for authorizing with multiple accounts from the same instance.
     * @param languageCode The ISO 639-1 two-letter language code to use while rendering the authorization form.
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#authorize">Mastodon oauth API methods #authorize</a>
     */
    @JvmOverloads
    fun getOAuthUrl(
        clientId: String,
        redirectUri: String,
        scope: Scope? = null,
        state: String? = null,
        codeChallenge: String? = null,
        codeChallengeMethod: String? = null,
        forceLogin: Boolean? = null,
        languageCode: String? = null
    ): Single<String> = Single.fromCallable {
        oAuthMethods.getOAuthUrl(
            clientId,
            redirectUri,
            scope,
            state,
            codeChallenge,
            codeChallengeMethod,
            forceLogin,
            languageCode
        )
    }

    /**
     * Obtain a user access token using OAuth 2 authorization code grant type. To be used during API calls that are not public.
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param redirectUri Set a URI to redirect the user to. Must match one of the redirect_uris declared during app registration.
     * @param code A user authorization code, obtained via the URL received from getOAuthUrl()
     * @param codeVerifier Required if PKCE is used during the authorization request. This is the code verifier which was used
     *  to create the `codeChallenge` using the `codeChallengeMethod` for the authorization request.
     * @param scope Requested OAuth scopes. Must be equal to the scope requested from the user while obtaining [code].
     *  If not provided, defaults to read.
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#token">Mastodon oauth API methods #token</a>
     */
    @JvmOverloads
    fun getUserAccessTokenWithAuthorizationCodeGrant(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        codeVerifier: String? = null,
        scope: Scope? = null
    ): Single<Token> = Single.fromCallable {
        oAuthMethods.getUserAccessTokenWithAuthorizationCodeGrant(
            clientId,
            clientSecret,
            redirectUri,
            code,
            codeVerifier,
            scope
        ).execute()
    }

    /**
     * Obtain an access token using OAuth 2 client credentials grant type. To be used during API calls that are not public.
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param redirectUri Set a URI to redirect the user to. Must match one of the redirect_uris declared during app registration.
     * @param scope Requested OAuth scopes. Must be a subset of scopes declared during app registration.
     *  If not provided, defaults to read.
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#token">Mastodon oauth API methods #token</a>
     * @see <a href="https://docs.joinmastodon.org/client/token/#methods">Usage of this authentication form</a>
     */
    @JvmOverloads
    fun getAccessTokenWithClientCredentialsGrant(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        scope: Scope? = null
    ): Single<Token> = Single.fromCallable {
        oAuthMethods.getAccessTokenWithClientCredentialsGrant(
            clientId,
            clientSecret,
            redirectUri,
            scope
        ).execute()
    }

    /**
     * Obtain a user access token using OAuth 2 password grant type, to be used for bots and other single-user applications.
     * Where possible, [getUserAccessTokenWithAuthorizationCodeGrant] should be used instead.
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param redirectUri Set a URI to redirect the user to.
     * @param username The Mastodon account username.
     * @param password The Mastodon account password.
     * @param scope Requested OAuth scopes. Must be a subset of scopes declared during app registration.
     *  If not provided, defaults to read.
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#token">Mastodon oauth API methods #token</a>
     */
    @JvmOverloads
    fun getUserAccessTokenWithPasswordGrant(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        username: String,
        password: String,
        scope: Scope? = null
    ): Single<Token> = Single.fromCallable {
        oAuthMethods.getUserAccessTokenWithPasswordGrant(
            clientId,
            clientSecret,
            redirectUri,
            username,
            password,
            scope
        ).execute()
    }

    /**
     * Revoke a token.
     *
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param token The previously obtained token, to be invalidated.
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#revoke">Mastodon oauth API methods #revoke</a>
     */
    fun revokeToken(
        clientId: String,
        clientSecret: String,
        token: String
    ): Completable = Completable.fromAction { oAuthMethods.revokeToken(clientId, clientSecret, token) }
}
