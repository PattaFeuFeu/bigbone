package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.DateTimeSerializer
import social.bigbone.PrecisionDateTime
import social.bigbone.PrecisionDateTime.InvalidPrecisionDateTime
import social.bigbone.api.entity.data.Visibility

/**
 * Represents a status posted by an account.
 * @see <a href="https://docs.joinmastodon.org/entities/Status/">Mastodon API Status</a>
 */
@Serializable
data class Status(
    /**
     * ID of the status in the database.
     */
    @SerialName("id")
    val id: String = "0",

    /**
     * URI of the status used for federation.
     */
    @SerialName("uri")
    val uri: String = "",

    /**
     * The date when this status was created.
     */
    @SerialName("created_at")
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable,

    /**
     * The account that authored this status.
     */
    @SerialName("account")
    val account: Account? = null,

    /**
     * HTML-encoded status content.
     */
    @SerialName("content")
    val content: String = "",

    /**
     * Visibility of this status.
     * @see Visibility
     */
    @SerialName("visibility")
    val visibility: Visibility = Visibility.PUBLIC,

    /**
     * Is this status marked as sensitive content?
     */
    @SerialName("sensitive")
    val isSensitive: Boolean = false,

    /**
     * Subject or summary line, below which status content is collapsed until expanded.
     */
    @SerialName("spoiler_text")
    val spoilerText: String = "",

    /**
     * Media that is attached to this status.
     */
    @SerialName("media_attachments")
    val mediaAttachments: List<MediaAttachment> = emptyList(),

    /**
     * The application used to post this status (optional).
     */
    @SerialName("application")
    val application: Application? = null,

    /**
     * Mentions of users within the status content.
     */
    @SerialName("mentions")
    val mentions: List<Mention> = emptyList(),

    /**
     * Hashtags used within the status content.
     */
    @SerialName("tags")
    val tags: List<Tag> = emptyList(),

    /**
     * Custom emoji to be used when rendering status content.
     */
    @SerialName("emojis")
    val emojis: List<CustomEmoji> = emptyList(),

    /**
     * How many boosts this status has received.
     */
    @SerialName("reblogs_count")
    val reblogsCount: Long = 0,

    /**
     * How many favourites this status has received.
     */
    @SerialName("favourites_count")
    val favouritesCount: Long = 0,

    /**
     * How many replies this status has received.
     */
    @SerialName("replies_count")
    val repliesCount: Long = 0,

    /**
     * A link to the status’s HTML representation.
     */
    @SerialName("url")
    val url: String = "",

    /**
     * ID of the status being replied to.
     */
    @SerialName("in_reply_to_id")
    val inReplyToId: String? = null,

    /**
     * ID of the account that authored the status being replied to.
     */
    @SerialName("in_reply_to_account_id")
    val inReplyToAccountId: String? = null,

    /**
     * The status being reblogged.
     */
    @SerialName("reblog")
    val reblog: Status? = null,

    /**
     * The poll attached to the status.
     */
    @SerialName("poll")
    val poll: Poll? = null,

    /**
     * Preview card for links included within status content.
     */
    @SerialName("card")
    val card: PreviewCard? = null,

    /**
     * Primary language of this status (ISO 639 Part 1 two-letter language code).
     */
    @SerialName("language")
    val language: String? = null,

    /**
     * Plain-text source of a status. Returned instead of content when status is deleted, so the user may redraft from
     * the source text without the client having to reverse-engineer the original text from the HTML content.
     */
    @SerialName("text")
    val text: String? = null,

    /**
     * Timestamp of when the status was last edited.
     */
    @SerialName("edited_at")
    @Serializable(with = DateTimeSerializer::class)
    val editedAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable,

    /**
     * If the current token has an authorized user: Have you favourited this status? (optional)
     */
    @SerialName("favourited")
    val isFavourited: Boolean = false,

    /**
     * If the current token has an authorized user: Have you boosted this status? (optional)
     */
    @SerialName("reblogged")
    val isReblogged: Boolean = false,

    /**
     * If the current token has an authorized user: Have you muted notifications for this status’s conversation? (optional)
     */
    @SerialName("muted")
    val isMuted: Boolean = false,

    /**
     * If the current token has an authorized user: Have you bookmarked this status? (optional)
     */
    @SerialName("bookmarked")
    val isBookmarked: Boolean = false,

    /**
     * If the current token has an authorized user: Have you pinned this status? A value of false does not imply that
     * this status is in fact pinnable.
     */
    @SerialName("pinned")
    val isPinned: Boolean = false,

    /**
     * The filter and keywords that matched this status. If no filters matched, this list will be empty.
     * May be null if the current token does not have an authorized user. (optional)
     */
    @SerialName("filtered")
    val filtered: List<FilterResult>? = null
) {

    /**
     * Mentions of users within the status content.
     * @see <a href="https://docs.joinmastodon.org/entities/Status/#Mention">Mastodon API Status::Mention</a>
     */
    @Serializable
    data class Mention(
        /**
         * The location of the mentioned user’s profile.
         */
        @SerialName("url")
        val url: String = "",

        /**
         * The username of the mentioned user.
         */
        @SerialName("username")
        val username: String = "",

        /**
         * The webfinger acct: URI of the mentioned user. Equivalent to username for local users, or username@domain for remote users.
         */
        @SerialName("acct")
        val acct: String = "",

        /**
         * The account ID of the mentioned user.
         */
        @SerialName("id")
        val id: String = "0"
    )
}
