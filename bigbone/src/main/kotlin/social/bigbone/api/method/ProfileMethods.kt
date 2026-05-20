package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.api.entity.CredentialAccount
import social.bigbone.api.entity.Profile

/**
 * Allows access to API methods with endpoints having an "api/vX/profile" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/profile/">Mastodon profile API methods</a>
 */
class ProfileMethods(private val client: MastodonClient) {

    private val endpoint = "api/v1/profile"

    /**
     * Returns the current user’s profile.
     */
    fun getUserProfile(): MastodonRequest<Profile> {
        return client.getMastodonRequest(
            endpoint = endpoint,
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Deletes the avatar associated with the user’s profile.
     */
    fun deleteProfileAvatar(): MastodonRequest<CredentialAccount> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/avatar",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * Deletes the header image associated with the user’s profile.
     */
    fun deleteProfileHeader(): MastodonRequest<CredentialAccount> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/header",
            method = MastodonClient.Method.DELETE
        )
    }
}
