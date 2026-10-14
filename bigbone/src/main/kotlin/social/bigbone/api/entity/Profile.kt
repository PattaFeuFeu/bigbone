package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.DateTimeSerializer
import social.bigbone.PrecisionDateTime
import social.bigbone.PrecisionDateTime.InvalidPrecisionDateTime

/**
 * Represents the current user's profile, with source values for all the editable fields.
 * @since Mastodon 4.6.0, API version 8
 */
@Serializable
data class Profile(
    /**
     * The account id.
     * String cast from an Integer, but not guaranteed to be a number.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("id")
    val id: String = "0",

    /**
     * The profile’s display name.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("display_name")
    val displayName: String = "",

    /**
     * The profile’s bio or description. Unlike for Account, this is the raw unprocessed text, not the rendered HTML.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("note")
    val note: String = "",

    /**
     * Metadata about the account. Those contain the raw unprocessed names and values.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("fields")
    val fields: List<Field> = emptyList(),

    /**
     * An image icon that is shown next to statuses and in the profile.
     * Unlike for [Account], this is nullable and will be null if the avatar is unset.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("avatar")
    val avatar: String? = null,

    /**
     * A static version of the avatar. Unlike for [Account], this is nullable and will be null if the avatar is unset.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("avatar_static")
    val avatarStatic: String? = null,

    /**
     * A textual description of the avatar, to be used for the visually impaired or when avatars do not load.
     *
     * @since Mastodon 4.6.0, API version 9
     */
    @SerialName("avatar_description")
    val avatarDescription: String = "",

    /**
     * An image banner that is shown above the profile and in profile cards.
     * Unlike for [Account], this is nullable and will be null if the header is unset.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("header")
    val header: String? = null,

    /**
     * A static version of the header. Unlike for Account, this is nullable and will be null if the header is unset.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("header_static")
    val headerStatic: String? = null,

    /**
     * A textual description of the profile header, to be used for the visually impaired or when avatars do not load.
     *
     * @since Mastodon 4.6.0, API version 9
     */
    @SerialName("header_description")
    val headerDescription: String = "",

    /**
     * Whether the account manually approves follow requests.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("locked")
    val locked: Boolean,

    /**
     * Indicates that the account may perform automated actions, may not be monitored, or identifies as a robot.
     * This is determined by the account’s actor_type being set to ‘Application’ or ‘Service’.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("bot")
    val bot: Boolean,

    /**
     * Whether the user hides the contents of their follows and followers collections.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("hide_collections")
    val hideCollections: Boolean? = null,

    /**
     * Whether the account has opted into discovery features such as the profile directory.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("discoverable")
    val discoverable: Boolean? = null,

    /**
     * Whether the account allows indexing by search engines.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("indexable")
    val indexable: Boolean,

    /**
     * Whether the account wishes to have a “Media” tab with media attachments on their profile.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("show_media")
    val showMedia: Boolean,

    /**
     * Whether the account wishes to have replies in the “Media” tab on their profile.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("show_media_replies")
    val showMediaReplies: Boolean,

    /**
     * Whether the account wishes to have a “Featured” tab on their profile.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("show_featured")
    val showFeatured: Boolean,

    /**
     * Domains of websites allowed to credit the account.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @SerialName("attribution_domains")
    val attributionDomains: List<String> = emptyList()
) {
    /**
     * Specifies a name-value pair as used in [fields] of the [Profile] entity.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    @Serializable
    data class Field(
        /**
         * The key of a given field’s key-value pair. This is the raw string before processing, not HTML.
         *
         * @since Mastodon 4.6.0, API version 8
         */
        @SerialName("name")
        val name: String = "",

        /**
         * The value associated with the [name] key. This is the raw string before processing, not HTML.
         *
         * @since Mastodon 4.6.0, API version 8
         */
        @SerialName("value")
        val value: String = "",

        /**
         * Timestamp of when the server verified a URL value for a rel=“me” link.
         * [InvalidPrecisionDateTime.Unavailable] if [value] is not a verified URL.
         *
         * @since Mastodon 4.6.0, API version 8
         */
        @SerialName("verified_at")
        @Serializable(with = DateTimeSerializer::class)
        val verifiedAt: PrecisionDateTime = InvalidPrecisionDateTime.Unavailable
    )
}
