package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.DateTimeSerializer
import social.bigbone.PrecisionDateTime
import social.bigbone.PrecisionDateTime.InvalidPrecisionDateTime
import social.bigbone.PrecisionDateTime.ValidPrecisionDateTime.ExactTime
import social.bigbone.api.entity.data.Visibility
import social.bigbone.api.method.AccountMethods

/**
 * Represents a user of Mastodon and their associated profile, as returned by certain Mastodon endpoints.
 * To use as a standard [Account], call [toAccount].
 * @see <a href="https://docs.joinmastodon.org/entities/Account/#CredentialAccount">Mastodon API Account#CredentialAccount</a>
 */
@Serializable
data class CredentialAccount(
    /**
     * The account id.
     * String cast from an Integer, but not guaranteed to be a number.
     */
    @SerialName("id")
    val id: String = "0",

    /**
     * The username of the account, not including domain.
     */
    @SerialName("username")
    val username: String = "",

    /**
     * The Webfinger account URI.
     * Equal to [username] for local users, or username@domain for remote users.
     */
    @SerialName("acct")
    val acct: String = "",

    /**
     * The location of the user’s profile page.
     */
    @SerialName("url")
    val url: String = "",

    /**
     * The profile’s display name.
     */
    @SerialName("display_name")
    val displayName: String = "",

    /**
     * The profile’s bio or description.
     * String containing HTML.
     */
    @SerialName("note")
    val note: String = "",

    /**
     * An image icon that is shown next to statuses and in the profile.
     * String containing a URL.
     */
    @SerialName("avatar")
    val avatar: String = "",

    /**
     * A static version of the avatar.
     * Equal to [avatar] if its value is a static image; different if [avatar] is an animated GIF.
     * String containing a URL.
     */
    @SerialName("avatar_static")
    val avatarStatic: String = "",

    /**
     * An image banner that is shown above the profile and in profile cards.
     * String containing a URL.
     */
    @SerialName("header")
    val header: String = "",

    /**
     * A static version of the header.
     * Equal to [header] if its value is a static image; different if [header] is an animated GIF.
     * String containing a URL.
     */
    @SerialName("header_static")
    val headerStatic: String = "",

    /**
     * Whether the account manually approves follow requests.
     */
    @SerialName("locked")
    val isLocked: Boolean = false,

    /**
     * Additional metadata attached to a profile as name-value pairs.
     */
    @SerialName("fields")
    val fields: List<Account.Field> = emptyList(),

    /**
     * Custom emoji entities to be used when rendering the profile.
     */
    @SerialName("emojis")
    val emojis: List<CustomEmoji> = emptyList(),

    /**
     * Indicates that the account may perform automated actions, may not be monitored, or identifies as a robot.
     */
    @SerialName("bot")
    val isBot: Boolean = false,

    /**
     * Indicates that the account represents a Group actor.
     */
    @SerialName("group")
    val isGroup: Boolean = false,

    /**
     * Whether the account has opted into discovery features such as the profile directory.
     */
    @SerialName("discoverable")
    val isDiscoverable: Boolean? = null,

    /**
     * Whether the local user has opted out of being indexed by search engines.
     */
    @SerialName("noindex")
    val isNotIndexed: Boolean? = null,

    /**
     * Indicates that the profile is currently inactive and that its user has moved to a new account.
     */
    @SerialName("moved")
    val moved: CredentialAccount? = null,

    /**
     * An extra attribute returned only when an account is suspended.
     */
    @SerialName("suspended")
    val isSuspended: Boolean? = null,

    /**
     * An extra attribute returned only when an account is silenced.
     * If true, indicates that the account should be hidden behind a warning screen.
     */
    @SerialName("limited")
    val isLimited: Boolean? = null,

    /**
     * When the account was created.
     * Resolves to midnight of the creation date instead of an exact time even if [ExactTime] is returned.
     */
    @SerialName("created_at")
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable,

    /**
     * When the most recent status was posted.
     * Will only have day-accuracy as no time is returned by the Mastodon API.
     */
    @SerialName("last_status_at")
    @Serializable(with = DateTimeSerializer::class)
    val lastStatusAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable,

    /**
     * How many statuses are attached to this account.
     */
    @SerialName("statuses_count")
    val statusesCount: Long = 0,

    /**
     * The reported followers of this profile.
     */
    @SerialName("followers_count")
    val followersCount: Long = 0,

    /**
     * The reported follows of this profile.
     */
    @SerialName("following_count")
    val followingCount: Long = 0,

    /**
     * Domains of websites allowed to credit the account.
     * @since Mastodon 4.4.0
     */
    @SerialName("attribution_domains")
    val attributionDomains: List<String> = emptyList(),

    /**
     * An extra attribute that contains source values
     * to be used with API methods that [AccountMethods.verifyCredentials] and [AccountMethods.updateCredentials].
     */
    @SerialName("source")
    val source: Source,

    /**
     * The role assigned to the currently authorized user.
     */
    @SerialName("role")
    val role: Role? = null
) {
    /**
     * An extra attribute that contains source values
     * to be used with API methods that [AccountMethods.verifyCredentials] and [AccountMethods.updateCredentials].
     * @see source
     */
    @Serializable
    data class Source(
        /**
         * Profile bio.
         * String containing plain-text content instead of HTML.
         */
        @SerialName("note")
        val note: String,

        /**
         * Metadata about the account.
         */
        @SerialName("fields")
        val fields: List<Account.Field>,

        /**
         * The default post privacy to be used for new statuses.
         */
        @SerialName("privacy")
        val privacy: Visibility,

        /**
         * Whether new statuses should be marked sensitive by default.
         */
        @SerialName("sensitive")
        val sensitive: Boolean,

        /**
         * The default posting language for new statuses.
         * String with ISO 639-1 language two-letter code or empty.
         */
        @SerialName("language")
        val language: String = "",

        /**
         * The number of pending follow requests.
         */
        @SerialName("follow_requests_count")
        val followRequestsCount: Int
    )

    /**
     * Returns an [Account] entity with the same attributes as this.
     */
    fun toAccount(): Account {
        return Account(
            id = id,
            username = username,
            acct = acct,
            url = url,
            displayName = displayName,
            note = note,
            avatar = avatar,
            avatarStatic = avatarStatic,
            header = header,
            headerStatic = headerStatic,
            isLocked = isLocked,
            fields = fields,
            emojis = emojis,
            isBot = isBot,
            isGroup = isGroup,
            isDiscoverable = isDiscoverable,
            isNotIndexed = isNotIndexed,
            moved = moved?.toAccount(),
            isSuspended = isSuspended,
            isLimited = isLimited,
            createdAt = createdAt,
            lastStatusAt = lastStatusAt,
            statusesCount = statusesCount,
            followersCount = followersCount,
            followingCount = followingCount,
        )
    }
}
