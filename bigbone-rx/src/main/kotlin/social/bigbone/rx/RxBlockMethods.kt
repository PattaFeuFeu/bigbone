package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Account
import social.bigbone.api.method.BlockMethods

/**
 * Reactive implementation of [BlockMethods].
 * Allows access to API methods with endpoints having an "api/vX/blocks" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/blocks/">Mastodon blocks API methods</a>
 */
class RxBlockMethods(client: MastodonClient) {

    private val blockMethods = BlockMethods(client)

    @JvmOverloads
    fun getBlocks(range: Range = Range()): Single<Pageable<Account>> = Single.fromCallable {
        blockMethods.getBlocks(range).execute()
    }
}
