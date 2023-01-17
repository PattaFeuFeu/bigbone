package social.bigbone.rx

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Notification
import social.bigbone.api.method.NotificationsMethods

class RxNotificationsMethods(client: MastodonClient) {
    val notificationsMethods = NotificationsMethods(client)

    fun getNotifications(range: Range): Single<Pageable<Notification>> {
        return Single.create {
            try {
                val notifications = notificationsMethods.getNotifications(range)
                it.onSuccess(notifications.execute())
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

    fun getNotification(id: String): Single<Notification> {
        return Single.create {
            try {
                val notification = notificationsMethods.getNotification(id)
                it.onSuccess(notification.execute())
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

    fun clearNotifications(): Completable {
        return Completable.create {
            try {
                notificationsMethods.clearNotifications()
                it.onComplete()
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }
}
