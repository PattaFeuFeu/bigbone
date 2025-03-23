package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.Scope
import social.bigbone.api.entity.Token

/**
 * Allows access to API methods with endpoints having an "oauth" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/oauth/">Mastodon oauth API methods</a>
 */
class OAuthMethods(private val client: MastodonClient) {

    private val endpoint = "oauth"

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
    ): String {
        val endpoint = "$endpoint/authorize"
        val params = Parameters().apply {
            append("client_id", clientId)
            append("redirect_uri", redirectUri)
            append("response_type", "code")
            scope?.let { append("scope", it.toString()) }
            state?.let { append("state", it) }
            codeChallenge?.let { append("code_challenge", it) }
            codeChallengeMethod?.let { append("code_challenge_method", it) }
            forceLogin?.let { append("force_login", it) }
            languageCode?.let { append("lang", it) }
        }

        return MastodonClient
            .fullUrl(
                scheme = client.getScheme(),
                instanceName = client.getInstanceName(),
                port = client.getPort(),
                path = endpoint,
                query = params
            )
            .toString()
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
    ): MastodonRequest<Token> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/token",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("redirect_uri", redirectUri)
                append("code", code)
                append("grant_type", GrantTypes.AUTHORIZATION_CODE.value)
                codeVerifier?.let { append("code_verifier", it) }
                scope?.let { append("scope", it.toString()) }
            }
        )
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
    ): MastodonRequest<Token> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/token",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("redirect_uri", redirectUri)
                scope?.let { append("scope", it.toString()) }
                append("grant_type", GrantTypes.CLIENT_CREDENTIALS.value)
            }
        )
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
    ): MastodonRequest<Token> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/token",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("redirect_uri", redirectUri)
                append("username", username)
                append("password", password)
                append("grant_type", GrantTypes.PASSWORD.value)
                scope?.let { append("scope", it.toString()) }
            }
        )
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
    ) {
        client.performAction(
            endpoint = "$endpoint/revoke",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("token", token)
            }
        )
    }

    companion object {
        /**
         * Possible grant types and respective parameter values used when requesting an access token.
         */
        private enum class GrantTypes(val value: String) {
            AUTHORIZATION_CODE("authorization_code"),
            CLIENT_CREDENTIALS("client_credentials"),
            PASSWORD("password")
        }
    }
}
