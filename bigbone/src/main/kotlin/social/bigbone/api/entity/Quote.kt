package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a quote or a quote placeholder, with the current authorization status.
 *
 * Note: This implements both the Quote and the ShallowQuote entity as defined by Mastodon.
 *
 * @see <a href="https://docs.joinmastodon.org/entities/Quote/">Mastodon API Quote</a>
 * @see <a href="https://docs.joinmastodon.org/entities/ShallowQuote/">Mastodon API ShallowQuote</a>
 */
@Serializable
data class Quote(
    /**
     *  The state of the quote.
     */
    @SerialName("state")
    val state: State,

    /**
     * The status being quoted, if the quote has been accepted. This will be null, unless the state attribute is accepted.
     * Note: only one of this and [quotedStatusId] will ever be non-null.
     */
    @SerialName("quoted_status")
    val quotedStatus: Status? = null,

    /**
     * The identifier of the status being quoted, if the quote has been accepted. This will be null, unless the state attribute is accepted.
     * Note: only one of this and quotedStatus will ever be non-null.
     */
    @SerialName("quoted_status_id")
    val quotedStatusId: String? = null
) {
    /**
     * The action to be taken when a status matches this filter.
     */
    @Serializable
    enum class State {
        /**
         * The quote has not been acknowledged by the quoted account yet, and requires authorization before being displayed.
         */
        @SerialName("pending")
        PENDING,

        /**
         * The quote has been accepted and can be displayed. This is the only case where one of quotedStatus or quotedStatusId is non-null.
         */
        @SerialName("accepted")
        ACCEPTED,

        /**
         * The quote has been explicitly rejected by the quoted account, and cannot be displayed.
         */
        @SerialName("rejected")
        REJECTED,

        /**
         * The quote has been previously accepted, but is now revoked, and thus cannot be displayed.
         */
        @SerialName("revoked")
        REVOKED,

        /**
         * The quote has been approved, but the quoted post itself has now been deleted.
         */
        @SerialName("deleted")
        DELETED,

        /**
         * The quote has been approved, but cannot be displayed because the user is not authorized to see it.
         */
        @SerialName("unauthorized")
        UNAUTHORIZED
    }
}
