package social.bigbone.api.method

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameter
import social.bigbone.api.Scope
import social.bigbone.api.entity.auth.AccessToken
import social.bigbone.api.entity.auth.AppRegistration

/**
 * Register client applications that can be used to obtain OAuth tokens.
 * @see <a href="https://docs.joinmastodon.org/methods/apps/">Mastodon apps API methods</a>
 *
 * Generate and manage OAuth tokens.
 * @see <a href="https://docs.joinmastodon.org/methods/oauth/">Mastodon oauth API methods</a>
 */
class Apps(private val client: MastodonClient) {

    /**
     * Create a new application to obtain OAuth2 credentials.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/apps/#create">Mastodon apps API methods #create</a>
     */
    @JvmOverloads
    fun createApp(
        clientName: String,
        redirectUris: String = "urn:ietf:wg:oauth:2.0:oob",
        scope: Scope = Scope(Scope.Name.ALL),
        website: String? = null
    ): MastodonRequest<AppRegistration> {
        scope.validate()
        return MastodonRequest(
            {
                client.post(
                    "api/v1/apps",
                    arrayListOf(
                        "client_name=$clientName",
                        "scopes=$scope",
                        "redirect_uris=$redirectUris"
                    ).apply {
                        website?.let {
                            add("website=$it")
                        }
                    }.joinToString(separator = "&")
                        .toRequestBody("application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull())
                )
            },
            {
                client.getSerializer().fromJson(it, AppRegistration::class.java).apply {
                    this.instanceName = client.getInstanceName()
                }
            }
        )
    }

    /**
     * Returns a URL that can be used to display an authorization form to the user. If approved,
     * it will create and return an authorization code, then redirect to the desired redirectUri,
     * or show the authorization code if urn:ietf:wg:oauth:2.0:oob was requested.
     * The authorization code can be used while requesting a token to obtain access to user-level methods.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#authorize">Mastodon oauth API methods #authorize</a>
     */
    fun getOAuthUrl(clientId: String, scope: Scope, redirectUri: String = "urn:ietf:wg:oauth:2.0:oob"): String {
        val endpoint = "/oauth/authorize"
        val parameters = listOf(
            "client_id=$clientId",
            "redirect_uri=$redirectUri",
            "response_type=code",
            "scope=$scope"
        ).joinToString(separator = "&")
        return "https://${client.getInstanceName()}$endpoint?$parameters"
    }

    /**
     * Obtain an access token, to be used during API calls that are not public.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/oauth/#token">Mastodon oauth API methods #token</a>
     */
    @JvmOverloads
    fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String = "urn:ietf:wg:oauth:2.0:oob",
        code: String,
        grantType: String = "authorization_code"
    ): MastodonRequest<AccessToken> {
        val url = "https://${client.getInstanceName()}/oauth/token"
        val parameters = listOf(
            "client_id=$clientId",
            "client_secret=$clientSecret",
            "redirect_uri=$redirectUri",
            "code=$code",
            "grant_type=$grantType"
        ).joinToString(separator = "&")
        return MastodonRequest(
            {
                client.postUrl(
                    url,
                    parameters
                        .toRequestBody("application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull())
                )
            },
            {
                client.getSerializer().fromJson(it, AccessToken::class.java)
            }
        )
    }

    /**
     * Obtain an access token, to be used during API calls that are not public. This method uses a grant_type
     * that is undocumented in Mastodon API, and should NOT be used in production code. It exists for example code
     * in this project and will be removed at a later date. getAccessToken() should be used instead.
     */
    @Deprecated(
        "This method uses grant_type 'password' which is undocumented in Mastodon API" +
            " and should not be used in production code.",
        ReplaceWith("getAccessToken()"),
        DeprecationLevel.WARNING
    )
    fun postUserNameAndPassword(
        clientId: String,
        clientSecret: String,
        scope: Scope,
        userName: String,
        password: String
    ): MastodonRequest<AccessToken> {
        val url = "https://${client.getInstanceName()}/oauth/token"
        val parameters = Parameter().apply {
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("scope", scope.toString())
            append("username", userName)
            append("password", password)
            append("grant_type", "password")
        }.build()

        return MastodonRequest(
            {
                client.postUrl(
                    url,
                    parameters
                        .toRequestBody("application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull())
                )
            },
            {
                client.getSerializer().fromJson(it, AccessToken::class.java)
            }
        )
    }
}
