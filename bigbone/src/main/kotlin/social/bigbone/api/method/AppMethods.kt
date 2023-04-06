package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.Scope
import social.bigbone.api.entity.Application

/**
 * Allows access to API methods with endpoints having an "api/vX/apps" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/apps/">Mastodon apps API methods</a>
 */
class AppMethods(private val client: MastodonClient) {

    /**
     * Create a new application to obtain OAuth2 credentials.
     * @param clientName A name for your application
     * @param redirectUris Where the user should be redirected after authorization.
     * @param scope Space separated list of scopes. Defaults to all scopes.
     * @param website A URL to the homepage of your app, defaults to null.
     * @see <a href="https://docs.joinmastodon.org/methods/apps/#create">Mastodon apps API methods #create</a>
     */
    @JvmOverloads
    fun createApp(
        clientName: String,
        redirectUris: String,
        scope: Scope = Scope(Scope.Name.ALL),
        website: String? = null
    ): MastodonRequest<Application> {
        scope.validate()
        return client.getMastodonRequest(
            endpoint = "api/v1/apps",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("client_name", clientName)
                append("scopes", scope.toString())
                append("redirect_uris", redirectUris)
                website?.let {
                    append("website", it)
                }
            }
        )
    }

    /**
     * Confirm that the app’s OAuth2 credentials work.
     * @see <a href="https://docs.joinmastodon.org/methods/apps/#verify_credentials">Mastodon API documentation: methods/apps/#verify_credentials</a>
     */
    fun verifyCredentials(): MastodonRequest<Application> {
        return client.getMastodonRequest(
            endpoint = "api/v1/apps/verify_credentials",
            method = MastodonClient.Method.GET
        )
    }
}
