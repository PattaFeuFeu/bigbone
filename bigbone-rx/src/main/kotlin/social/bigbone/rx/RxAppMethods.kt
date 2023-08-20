package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Scope
import social.bigbone.api.entity.Application
import social.bigbone.api.method.AppMethods
import social.bigbone.rx.extensions.onErrorIfNotDisposed

/**
 * Reactive implementation of [AppMethods].
 * Allows access to API methods with endpoints having an "api/vX/apps" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/apps/">Mastodon apps API methods</a>
 */
class RxAppMethods(client: MastodonClient) {
    private val appMethods = AppMethods(client)

    @JvmOverloads
    fun createApp(
        clientName: String,
        redirectUris: String,
        website: String? = null,
        scope: Scope = Scope(Scope.Name.ALL)
    ): Single<Application> {
        return Single.create {
            try {
                val application = appMethods.createApp(clientName, redirectUris, website, scope)
                it.onSuccess(application.execute())
            } catch (throwable: Throwable) {
                it.onErrorIfNotDisposed(throwable)
            }
        }
    }
}
