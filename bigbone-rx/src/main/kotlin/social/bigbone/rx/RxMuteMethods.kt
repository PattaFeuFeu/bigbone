package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.MutedAccount
import social.bigbone.api.method.MuteMethods

/**
 * Reactive implementation of [MuteMethods].
 * Allows access to API methods with endpoints having an "api/vX/mutes" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/mutes/">Mastodon mutes API methods</a>
 */
class RxMuteMethods(client: MastodonClient) {

    private val muteMethods = MuteMethods(client)

    /**
     * Accounts the user has muted.
     * @param range optional Range for the pageable return value
     * @see <a href="https://docs.joinmastodon.org/methods/mutes/#get">Mastodon API documentation: methods/mutes/#get</a>
     */
    @JvmOverloads
    fun getMutes(range: Range = Range()): Single<Pageable<MutedAccount>> = Single.fromCallable {
        muteMethods.getMutes(range).execute()
    }
}
