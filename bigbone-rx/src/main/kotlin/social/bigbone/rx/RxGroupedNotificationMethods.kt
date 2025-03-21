package social.bigbone.rx

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Account
import social.bigbone.api.entity.GroupedNotificationsResults
import social.bigbone.api.entity.NotificationType
import social.bigbone.api.entity.UnreadNotificationCount
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.api.method.GroupedNotificationMethods
import social.bigbone.api.method.GroupedNotificationMethods.ExpandAccounts
import social.bigbone.api.method.NotificationMethods

/**
 * Reactive implementation of [GroupedNotificationMethods].
 * Allows access to API methods with endpoints having an "api/v2/notifications" prefix.
 *
 * This differs from [RxNotificationMethods] in that it offers grouping.
 *
 * Grouped notifications were implemented server-side so that:
 * 1. grouping is consistent across clients
 * 2. clients do not run into the issue of going through entire pages that do not
 * contribute to any new group; instead, notifications are already deduplicated server-side
 *
 * The API shape is a bit different from the non-grouped notifications,
 * because large notification groups usually tend to involve the same accounts,
 * and moving accounts to a root key can avoid a lot of duplication,
 * resulting in less server-side work and smaller network payloads.
 *
 * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/">Mastodon grouped notifications API methods</a>
 * @since Mastodon 4.3.0
 */
class RxGroupedNotificationMethods(client: MastodonClient) {

    private val groupedNotificationMethods = GroupedNotificationMethods(client)

    /**
     * Return grouped notifications concerning the user.
     *
     * Notifications of type favourite, follow or reblog with the same type and the same target
     * made in a similar timeframe are given a same group_key by the server, and querying this
     * endpoint will return aggregated notifications, with only one object per group_key.
     *
     * Other notification types may be grouped in the future.
     * The grouped_types parameter should be used by the client to explicitly list the types it
     * supports showing grouped notifications for.
     *
     * @param includeTypes Types to include in the results.
     * @param excludeTypes Types to exclude from the results.
     * @param accountId Return only notifications received from the specified account.
     * @param expandAccounts One of [ExpandAccounts.FULL] or [ExpandAccounts.PARTIAL_AVATARS].
     * When set to [ExpandAccounts.PARTIAL_AVATARS], some accounts will not be rendered in full
     * in the returned accounts list but will be instead returned in stripped-down form in the
     * partial_accounts list.
     * The most recent account in a notification group is always rendered in full in the accounts attribute.
     * @param groupedTypes Restricts which [NotificationType]s can be grouped.
     * Use this if there are notification types for which your client does not support grouping.
     * If omitted, the server will group notifications of all types it supports
     * (4.3.0: [NotificationType.FAVOURITE], [NotificationType.FOLLOW], [NotificationType.REBLOG]).
     * If you do not want any notification grouping, use [NotificationMethods.getAllNotifications] instead.
     * Notifications that would be grouped if not for this parameter will instead be returned as individual
     * single-notification groups with a unique group_key that can be assumed to be of the form "ungrouped-{notification_id}".
     * @param includeFiltered Whether to include notifications filtered by the user’s NotificationPolicy. Defaults to false.
     * @param range optional Range for the pageable return value.
     * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/#get-grouped">
     *     Mastodon API documentation: methods/grouped_notifications/#get-grouped</a>
     * @since Mastodon 4.3.0
     */
    @JvmOverloads
    fun getAllGroupedNotifications(
        includeTypes: List<NotificationType>? = null,
        excludeTypes: List<NotificationType>? = null,
        accountId: String? = null,
        expandAccounts: ExpandAccounts? = null,
        groupedTypes: List<NotificationType>? = null,
        includeFiltered: Boolean = false,
        range: Range = Range()
    ): Single<MastodonRequest<Pageable<GroupedNotificationsResults>>> = Single.fromCallable {
        groupedNotificationMethods.getAllGroupedNotifications(
            includeTypes = includeTypes,
            excludeTypes = excludeTypes,
            accountId = accountId,
            expandAccounts = expandAccounts,
            groupedTypes = groupedTypes,
            includeFiltered = includeFiltered,
            range = range
        )
    }

    /**
     * View information about a specific notification group with a given group key.
     * @param groupKey The group key of the notification group
     * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/#get-notification-group">
     *     Mastodon API documentation: methods/grouped_notifications/#get-notification-group</a>
     * @since Mastodon 4.3.0
     */
    fun getGroupedNotification(groupKey: String): Single<MastodonRequest<GroupedNotificationsResults>> = Single
        .fromCallable { groupedNotificationMethods.getGroupedNotification(groupKey = groupKey) }

    /**
     * Dismiss a single notification group from the server.
     * @param groupKey The group key of the notifications to discard.
     * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/#dismiss-group">
     *     Mastodon API documentation: methods/grouped_notifications/#dismiss-group</a>
     * @since Mastodon 4.3.0
     */
    @Throws(BigBoneRequestException::class)
    fun dismissNotification(groupKey: String) = Completable.fromAction {
        groupedNotificationMethods.dismissNotification(groupKey = groupKey)
    }

    /**
     * Get accounts of all notifications in a notification group.
     * @param groupKey The group key of the notifications to get accounts from.
     * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/#get-group-accounts">
     *     Mastodon API documentation: methods/grouped_notifications/#get-group-accounts</a>
     * @since Mastodon 4.3.0
     */
    fun getAccountsOfAllNotificationsInGroup(groupKey: String): Single<MastodonRequest<List<Account>>> = Single
        .fromCallable { groupedNotificationMethods.getAccountsOfAllNotificationsInGroup(groupKey = groupKey) }

    /**
     * Get the (capped) number of unread notification groups for the current user.
     * A notification is considered unread if it is more recent than the notifications read marker.
     * Because the count is dependent on the parameters, it is computed every time and is thus a relatively slow operation
     * (although faster than getting the full corresponding notifications), therefore the number of returned notifications is capped.
     * @param limit Maximum number of results to return. Defaults to 100 notifications. Max 1_000 notifications.
     * @param types [NotificationType]s that should count towards unread notifications.
     * @param excludeTypes [NotificationType]s that should not count towards unread notifications.
     * @param accountId Only count unread notifications received from the specified account.
     * @param groupedTypes Restrict which [NotificationType]s can be grouped.
     * Use this if there are [NotificationType]s for which your client does not support grouping.
     * If omitted, the server will group notifications of all types it supports
     * (4.3.0: [NotificationType.FAVOURITE], [NotificationType.FOLLOW], [NotificationType.REBLOG]).
     * If you do not want any notification grouping, use [NotificationMethods.getUnreadCount] instead.
     * @see <a href="https://docs.joinmastodon.org/methods/grouped_notifications/#unread-group-count">
     *     Mastodon API documentation: methods/grouped_notifications/#unread-group-count</a>
     * @since Mastodon 4.3.0
     * @throws IllegalArgumentException if [limit] is set and higher than 1_000.
     */
    @Throws(IllegalArgumentException::class)
    fun getNumberOfUnreadNotifications(
        limit: Int? = null,
        types: List<NotificationType>? = null,
        excludeTypes: List<NotificationType>? = null,
        accountId: String? = null,
        groupedTypes: List<NotificationType>? = null
    ): Single<MastodonRequest<UnreadNotificationCount>> = Single.fromCallable {
        groupedNotificationMethods.getNumberOfUnreadNotifications(
            limit = limit,
            types = types,
            excludeTypes = excludeTypes,
            accountId = accountId,
            groupedTypes = groupedTypes
        )
    }
}
