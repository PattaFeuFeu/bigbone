package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Scope
import social.bigbone.api.entity.auth.AccessToken
import social.bigbone.api.entity.auth.AppRegistration
import social.bigbone.api.method.Apps
import social.bigbone.rx.extensions.onErrorIfNotDisposed

class RxApps(client: MastodonClient) {
    val apps = Apps(client)

    fun createApp(
        clientName: String,
        redirectUris: String = "urn:ietf:wg:oauth:2.0:oob",
        scope: Scope,
        website: String? = null
    ): Single<AppRegistration> {
        return Single.create {
            try {
                val registration = apps.createApp(clientName, redirectUris, scope, website)
                it.onSuccess(registration.execute())
            } catch (throwable: Throwable) {
                it.onErrorIfNotDisposed(throwable)
            }
        }
    }

    fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String = "urn:ietf:wg:oauth:2.0:oob",
        code: String,
        grantType: String = "authorization_code"
    ): Single<AccessToken> {
        return Single.create {
            try {
                val accessToken = apps.getAccessToken(clientId, clientSecret, redirectUri, code, grantType)
                it.onSuccess(accessToken.execute())
            } catch (throwable: Throwable) {
                it.onErrorIfNotDisposed(throwable)
            }
        }
    }
}
