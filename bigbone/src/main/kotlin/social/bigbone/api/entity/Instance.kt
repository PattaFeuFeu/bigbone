package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.DateTimeSerializer
import social.bigbone.PrecisionDateTime

/**
 * Represents the software instance of Mastodon running on this domain.
 * @see <a href="https://docs.joinmastodon.org/entities/Instance/">Mastodon API Instance</a>
 */
@Serializable
data class Instance(
    /**
     * The domain name of the instance.
     */
    @SerialName("domain")
    val domain: String = "",

    /**
     * The title of the website.
     */
    @SerialName("title")
    val title: String = "",

    /**
     * The version of Mastodon installed on the instance.
     */
    @SerialName("version")
    val version: String = "",

    /**
     * The URL for the source code of the software running on this instance, in keeping with AGPL license requirements.
     */
    @SerialName("source_url")
    val sourceUrl: String = "",

    /**
     * A short, plain-text description defined by the admin.
     */
    @SerialName("description")
    val description: String = "",

    @SerialName("usage")
    val usage: Usage = Usage(),

    /**
     * Banner image for the website.
     */
    @SerialName("thumbnail")
    val thumbnail: Thumbnail = Thumbnail(),

    /**
     * Primary languages of the website and its staff.
     * [List] of [String] (ISO 639-1 two-letter code)
     */
    @SerialName("languages")
    val languages: List<String> = emptyList(),

    /**
     * Configured values and limits for this website.
     */
    @SerialName("configuration")
    val configuration: Configuration = Configuration(),

    /**
     * Information about registering for this website.
     */
    @SerialName("registrations")
    val registrations: Registrations = Registrations(),

    /**
     * Information about which version of the API is implemented by this server.
     */
    @SerialName("api_versions")
    val apiVersions: ApiVersions = ApiVersions(),

    /**
     * Hints related to contacting a representative of the website.
     */
    @SerialName("contact")
    val contact: Contact = Contact(),

    /**
     * An itemized list of rules for this website.
     */
    @SerialName("rules")
    val rules: List<Rule> = emptyList()
) {
    /**
     * Usage data for this instance.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#usage">Mastodon API Instance/#usage</a>
     */
    @Serializable
    data class Usage(
        /**
         * Usage data related to users on this instance.
         */
        @SerialName("users")
        val users: Users = Users()
    ) {
        /**
         * Usage data related to users on this instance.
         */
        @Serializable
        data class Users(
            /**
             * The number of active users in the past 4 weeks.
             */
            @SerialName("active_month")
            val activeMonth: Int = 0
        )
    }

    /**
     * An image used to represent this instance.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#thumbnail">Mastodon API Instance/#thumbnail</a>
     */
    @Serializable
    data class Thumbnail(
        /**
         * The URL for the thumbnail image.
         */
        @SerialName("url")
        val url: String = "",

        /**
         * A hash computed by <a href="https://github.com/woltapp/blurhash">the BlurHash algorithm</a>,
         * for generating colorful preview thumbnails when media has not been downloaded yet.
         */
        @SerialName("blurhash")
        val blurHash: String? = "",

        /**
         * Links to scaled resolution images, for high DPI screens.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#thumbnail-versions">Mastodon API Instance/#thumbnail-versions</a>
         */
        @SerialName("versions")
        val versions: Versions? = null
    ) {
        /**
         * Links to scaled resolution images, for high DPI screens.
         */
        @Serializable
        data class Versions(
            /**
             * The URL for the thumbnail image at 1x resolution.
             */
            @SerialName("@1x")
            val resolution1x: String? = null,

            /**
             * The URL for the thumbnail image at 2x resolution.
             */
            @SerialName("@2x")
            val resolution2x: String? = null
        )
    }

    /**
     * Configured values and limits for this website.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#configuration">Mastodon API Instance/#configuration</a>
     */
    @Serializable
    data class Configuration(
        /**
         * URLs of interest for clients apps.
         */
        @SerialName("urls")
        val urls: Urls = Urls(),

        /**
         * Limits related to accounts.
         */
        @SerialName("accounts")
        val accounts: Accounts = Accounts(),

        /**
         * Limits related to authoring statuses.
         */
        @SerialName("statuses")
        val statuses: Statuses = Statuses(),

        /**
         * Hints for which attachments will be accepted.
         */
        @SerialName("media_attachments")
        val mediaAttachments: MediaAttachments = MediaAttachments(),

        /**
         * Limits related to polls.
         */
        @SerialName("polls")
        val polls: Polls = Polls(),

        /**
         * Hints related to translation.
         */
        @SerialName("translation")
        val translation: Translation = Translation(),

        /**
         * Whether federation is limited to explicitly allowed domains.
         * @since Mastodon 4.4.0
         */
        @SerialName("limited_federation")
        val limitedFederation: Boolean = false,
    ) {
        /**
         * URLs of interest for clients apps.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#urls">Mastodon API Instance/#urls</a>
         */
        @Serializable
        data class Urls(
            /**
             * The Websockets URL for connecting to the streaming API.
             */
            @SerialName("streaming")
            val streaming: String = "",

            /**
             * The URL of the server’s about page.
             * @since Mastodon 4.4.0
             */
            @SerialName("about")
            val about: String = "",

            /**
             * The URL of the server’s privacy policy.
             * @since Mastodon 4.4.0
             */
            @SerialName("privacy_policy")
            val privacyPolicy: String? = null,

            /**
             * The URL of the server’s current terms of service, if any.
             * @since Mastodon 4.4.0
             */
            @SerialName("terms_of_service")
            val termsOfService: String? = null,
        )

        /**
         * Limits related to accounts.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#accounts">Mastodon API Instance/#accounts</a>
         */
        @Serializable
        data class Accounts(
            /**
             * The maximum number of featured tags allowed for each account.
             */
            @SerialName("max_featured_tags")
            val maxFeaturedTags: Int = 0
        )

        /**
         * Limits related to authoring statuses.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#statuses">Mastodon API Instance/#statuses</a>
         */
        @Serializable
        data class Statuses(
            /**
             * The maximum number of allowed characters per status.
             */
            @SerialName("max_characters")
            val maxCharacters: Int = 0,

            /**
             * The maximum number of media attachments that can be added to a status.
             */
            @SerialName("max_media_attachments")
            val maxMediaAttachments: Int = 0,

            /**
             * Each URL in a status will be assumed to be exactly this many characters.
             */
            @SerialName("characters_reserved_per_url")
            val charactersReservedPerUrl: Int = 0
        )

        /**
         * Hints for which attachments will be accepted.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#media_attachments">Mastodon API Instance/#media_attachments</a>
         */
        @Serializable
        data class MediaAttachments(
            /**
             * Contains MIME types that can be uploaded.
             */
            @SerialName("supported_mime_types")
            val supportedMimeTypes: List<String> = emptyList(),

            /**
             * The maximum size of a description, in characters.
             * @since Mastodon 4.4.0
             */
            @SerialName("description_limit")
            val descriptionLimit: Int = 0,

            /**
             * The maximum size of any uploaded image, in bytes.
             */
            @SerialName("image_size_limit")
            val imageSizeLimit: Int = 0,

            /**
             * The maximum number of pixels (width times height) for image uploads.
             */
            @SerialName("image_matrix_limit")
            val imageMatrixLimit: Int = 0,

            /**
             * The maximum size of any uploaded video, in bytes.
             */
            @SerialName("video_size_limit")
            val videoSizeLimit: Int = 0,

            /**
             * The maximum frame rate for any uploaded video.
             */
            @SerialName("video_frame_rate_limit")
            val videoFrameRateLimit: Int = 0,

            /**
             * The maximum number of pixels (width times height) for video uploads.
             */
            @SerialName("video_matrix_limit")
            val videoMatrixLimit: Int = 0
        )

        /**
         * Limits related to polls.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#polls">Mastodon API Instance/#polls</a>
         */
        @Serializable
        data class Polls(
            /**
             * Each poll is allowed to have up to this many options.
             */
            @SerialName("max_options")
            val maxOptions: Int = 0,

            /**
             * Each poll option is allowed to have this many characters.
             */
            @SerialName("max_characters_per_option")
            val maxCharactersPerOption: Int = 0,

            /**
             * The shortest allowed poll duration, in seconds.
             */
            @SerialName("min_expiration")
            val minExpiration: Int = 0,

            /**
             * The longest allowed poll duration, in seconds.
             */
            @SerialName("max_expiration")
            val maxExpiration: Int = 0
        )

        /**
         * Hints related to translation.
         * @see <a href="https://docs.joinmastodon.org/entities/Instance/#translation">Mastodon API Instance/#translation</a>
         */
        @Serializable
        data class Translation(
            /**
             * Whether the Translations API is available on this instance.
             */
            @SerialName("enabled")
            val enabled: Boolean = false
        )
    }

    /**
     * Information about registering for this website.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#registrations">Mastodon API Instance/#registrations</a>
     */
    @Serializable
    data class Registrations(
        /**
         * Whether registrations are enabled.
         */
        @SerialName("enabled")
        val enabled: Boolean = false,

        /**
         * Whether registrations require moderator approval.
         */
        @SerialName("approval_required")
        val approvalRequired: Boolean = false,

        /**
         * Whether registrations require the user to provide a reason for joining.
         * Only applicable when [approvalRequired] is true.
         * @since Mastodon 4.4.0
         */
        @SerialName("reason_required")
        val reasonRequired: Boolean? = false,

        /**
         * The minimum user age in years, if set.
         * @since Mastodon 4.4.0
         */
        @SerialName("min_age")
        val minAge: Int? = null,

        /**
         * A custom message to be shown when registrations are closed.
         * Nullable String (HTML) or null
         */
        @SerialName("message")
        val message: String? = null,
    )

    /**
     * Information about which version of the API is implemented by this server.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#api-versions">Mastodon API Instance/#api-versions</a>
     */
    @Serializable
    data class ApiVersions(
        /**
         * API version number that this server implements. Starting from Mastodon v4.3.0,
         * API changes will come with a version number, which clients can check against this value.
         */
        @SerialName("mastodon")
        val mastodon: Int = 0,
    )

    /**
     * Hints related to contacting a representative of the website.
     * @see <a href="https://docs.joinmastodon.org/entities/Instance/#contact">Mastodon API Instance/#contact</a>
     */
    @Serializable
    data class Contact(
        /**
         * An email address that can be messaged regarding inquiries or issues.
         */
        @SerialName("email")
        val email: String = "",

        /**
         * An account that can be contacted natively over the network regarding inquiries or issues.
         */
        @SerialName("account")
        val account: Account = Account()
    )
}

/**
 * Instance activity class that can be returned by e.g. <a href="https://docs.joinmastodon.org/methods/instance/#activity">methods/instance/#activity</a>
 */
@Serializable
data class InstanceActivity(
    /**
     * String (UNIX Timestamp). Midnight at the first day of the week.
     */
    @SerialName("week")
    val week: String = "",
    /**
     * String (cast from an integer). The number of Statuses created since the week began.
     */
    @SerialName("statuses")
    val statuses: String = "",
    /**
     * String (cast from an integer). The number of user logins since the week began.
     */
    @SerialName("logins")
    val logins: String = "",
    /**
     * String (cast from an integer). The number of user registrations since the week began.
     */
    @SerialName("registrations")
    val registrations: String = ""
)

/**
 * Represents a domain that is blocked by the instance.
 * @see <a href="https://docs.joinmastodon.org/entities/DomainBlock/">entities/DomainBlock</a>
 */
@Serializable
data class DomainBlock(
    /**
     * The domain which is blocked. This may be obfuscated or partially censored.
     */
    @SerialName("domain")
    val domain: String = "",

    /**
     * The SHA256 hash digest of the domain string.
     */
    @SerialName("digest")
    val digest: String = "",

    /**
     * The level to which the domain is blocked.
     */
    @SerialName("severity")
    val severity: Severity = Severity.SILENCE,

    /**
     * An optional reason for the domain block.
     */
    @SerialName("comment")
    val comment: String? = null
) {
    /**
     * The level to which the domain is blocked.
     */
    @Serializable
    enum class Severity {
        /**
         * Users from this domain will be hidden from timelines, threads, and notifications (unless you follow the user).
         */
        @SerialName("silence")
        SILENCE,

        /**
         * Incoming messages from this domain will be rejected and dropped entirely.
         */
        @SerialName("suspend")
        SUSPEND
    }
}

/**
 * Represents an extended description for the instance, to be shown on its about page.
 */
@Serializable
data class ExtendedDescription(
    /**
     * A timestamp of when the extended description was last updated.
     */
    @SerialName("updated_at")
    @Serializable(with = DateTimeSerializer::class)
    val updatedAt: PrecisionDateTime = PrecisionDateTime.InvalidPrecisionDateTime.Unavailable,

    /**
     * The rendered HTML content of the extended description.
     * String (HTML)
     */
    @SerialName("content")
    val content: String = ""
)
