package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.DateTimeSerializer
import social.bigbone.PrecisionDateTime
import social.bigbone.PrecisionDateTime.InvalidPrecisionDateTime

/**
 * Represents a user-defined filter for determining which statuses should not be shown to the user.
 * @see <a href="https://docs.joinmastodon.org/entities/Filter/">Mastodon API Filter</a>
 */
@Serializable
data class Filter(
    /**
     * The ID of the Filter in the database.
     */
    @SerialName("id")
    val id: String = "0",

    /**
     * A title given by the user to name the filter.
     */
    @SerialName("title")
    val title: String = "",

    /**
     * The contexts in which the filter should be applied.
     * @see FilterContext
     */
    @SerialName("context")
    val context: List<FilterContext> = emptyList(),

    /**
     * When the filter should no longer be applied.
     * [InvalidPrecisionDateTime.Unavailable] if the filter does not expire.
     */
    @SerialName("expires_at")
    @Serializable(with = DateTimeSerializer::class)
    val expiresAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable,

    /**
     * The action to be taken when a status matches this filter.
     * @see FilterAction
     */
    @SerialName("filter_action")
    val filterAction: FilterAction = FilterAction.WARN,

    /**
     * The keywords grouped under this filter.
     */
    @SerialName("keywords")
    val keywords: List<FilterKeyword> = emptyList(),

    /**
     * The statuses grouped under this filter.
     */
    @SerialName("statuses")
    val statuses: List<FilterStatus> = emptyList()
) {
    /**
     * The action to be taken when a status matches this filter.
     */
    @Serializable
    enum class FilterAction {
        /**
         * Do not show this status if it is received.
         */
        @SerialName("hide")
        HIDE,

        /**
         * Show a warning that identifies the matching filter by title, and allow the user to expand the filtered status.
         * This is the default (and unknown values should be treated as equivalent to warn).
         */
        @SerialName("warn")
        WARN,

        /**
         * Hide/blur media attachments with a warning identifying the matching filter by title.
         */
        @SerialName("blur")
        BLUR
    }

    /**
     * The context(s) in which the filter should be applied.
     */
    @Serializable
    enum class FilterContext {
        /**
         * Apply filter when viewing a profile.
         */
        @SerialName("account")
        ACCOUNT,

        /**
         * Apply filter when viewing home timeline and lists.
         */
        @SerialName("home")
        HOME,

        /**
         * Apply filter when viewing notifications timeline.
         */
        @SerialName("notifications")
        NOTIFICATIONS,

        /**
         * Apply filter when viewing public timelines.
         */
        @SerialName("public")
        PUBLIC,

        /**
         * Apply filter when viewing expanded thread of a detailed status.
         */
        @SerialName("thread")
        THREAD
    }
}
