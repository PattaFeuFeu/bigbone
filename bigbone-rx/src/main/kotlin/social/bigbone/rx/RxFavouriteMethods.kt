package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Status
import social.bigbone.api.method.FavouriteMethods

/**
 * Reactive implementation of [FavouriteMethods].
 * Allows access to API methods with endpoints having an "api/vX/favourites" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/favourites/">Mastodon favourites API methods</a>
 */
class RxFavouriteMethods(client: MastodonClient) {

    private val favouriteMethods = FavouriteMethods(client)

    @JvmOverloads
    fun getFavourites(range: Range = Range()): Single<Pageable<Status>> = Single.fromCallable {
        favouriteMethods.getFavourites(range).execute()
    }
}
