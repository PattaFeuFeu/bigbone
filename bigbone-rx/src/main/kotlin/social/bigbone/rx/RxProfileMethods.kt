package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.entity.CredentialAccount
import social.bigbone.api.entity.Profile
import social.bigbone.api.method.ProfileMethods

/**
 * Reactive implementation of [ProfileMethods].
 * Allows access to API methods with endpoints having an "api/vX/profile" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/profile/">Mastodon profile API methods</a>
 */
class RxProfileMethods(client: MastodonClient) {

    private val profileMethods = ProfileMethods(client)

    /**
     * Returns the current user’s profile.
     */
    fun getUserProfile(): Single<Profile> = Single.fromCallable {
        profileMethods.getUserProfile().execute()
    }

    /**
     * Deletes the avatar associated with the user’s profile.
     */
    fun deleteProfileAvatar(): Single<CredentialAccount> = Single.fromCallable {
        profileMethods.deleteProfileAvatar().execute()
    }

    /**
     * Deletes the header image associated with the user’s profile.
     */
    fun deleteProfileHeader(): Single<CredentialAccount> = Single.fromCallable {
        profileMethods.deleteProfileHeader().execute()
    }
}