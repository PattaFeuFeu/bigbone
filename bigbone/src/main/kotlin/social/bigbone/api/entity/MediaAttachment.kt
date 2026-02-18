package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.api.entity.data.Focus

/**
 * Represents a file or media attachment that can be added to a status.
 * @see <a href="https://docs.joinmastodon.org/entities/MediaAttachment/">Mastodon API MediaAttachment</a>
 */
@Serializable
data class MediaAttachment(
    /**
     * The ID of the attachment in the database.
     */
    @SerialName("id")
    val id: String = "0",

    /**
     * The type of the attachment.
     */
    @SerialName("type")
    val type: MediaType = MediaType.IMAGE,

    /**
     * The location of the original full-size attachment.
     */
    @SerialName("url")
    val url: String? = null,

    /**
     * The location of the full-size original attachment on the remote website.
     * May be null if the attachment is local.
     */
    @SerialName("remote_url")
    val remoteUrl: String? = null,

    /**
     * The location of a scaled-down preview of the attachment.
     */
    @SerialName("preview_url")
    val previewUrl: String = "",

    /**
     * Metadata returned by Paperclip.
     */
    @SerialName("meta")
    val meta: Meta? = null,

    /**
     * Alternate text that describes what is in the media attachment.
     * To be used for the visually impaired or when media attachments do not load.
     */
    @SerialName("description")
    val description: String? = null,

    /**
     * A hash computed by the BlurHash algorithm.
     * For generating colorful preview thumbnails when media has not been downloaded yet.
     */
    @SerialName("blurhash")
    val blurhash: String? = null
) {
    /**
     * Metadata returned by Paperclip.
     */
    @Serializable
    data class Meta(
        /**
         * Contains the coordinates to be used for smart thumbnail cropping.
         */
        @SerialName("focus")
        val focus: Focus? = null,

        /**
         * Duration of this media attachment as a String.
         * Only available for audio and video.
         */
        @SerialName("length")
        val length: String? = null,

        /**
         * Duration of this media attachment as a Float.
         * Only available for audio and video.
         */
        @SerialName("duration")
        val duration: Float? = null,

        /**
         * Frame rate of this media attachment in frames per second.
         * Only available for video or moving pictures.
         */
        @SerialName("fps")
        val frameRate: Int? = null,

        /**
         * Size of this media attachment.
         * Readable string version, like 640x480.
         */
        @SerialName("size")
        val size: String? = null,

        /**
         * Width of this media attachment.
         */
        @SerialName("width")
        val width: Int? = null,

        /**
         * Height of this media attachment.
         */
        @SerialName("height")
        val height: Int? = null,

        /**
         * Aspect ratio of this media attachment.
         */
        @SerialName("aspect")
        val aspectRatio: Double? = null,

        /**
         * Original, full-res media attachment size.
         */
        @SerialName("original")
        val original: MediaSize? = null,

        /**
         * Smaller, scaled-down version of the original media attachment.
         */
        @SerialName("small")
        val small: MediaSize? = null
    ) {
        /**
         * Represents size aspects of a media attachment, such as width and height, aspect ratio, bit rate, etc.
         */
        @Serializable
        data class MediaSize(
            /**
             * Width of this media attachment.
             */
            @SerialName("width")
            val width: Int? = null,

            /**
             * Height of this media attachment.
             */
            @SerialName("height")
            val height: Int? = null,

            /**
             * Size of this media attachment.
             * Readable string version, like 640x480.
             */
            @SerialName("size")
            val size: String? = null,

            /**
             * Aspect ratio of this media attachment.
             */
            @SerialName("aspect")
            val aspectRatio: Double? = null,

            /**
             * Duration of this media attachment as a Float.
             * Only available for audio and video.
             */
            @SerialName("duration")
            val duration: Float? = null,

            /**
             * Amount of information per seconds, or bit rate, of the media attachment.
             * Only available for audio and video.
             */
            @SerialName("bitrate")
            val bitRate: Int? = null
        )
    }

    /**
     * The available media types.
     */
    @Serializable
    enum class MediaType {
        @SerialName("audio")
        AUDIO,

        @SerialName("image")
        IMAGE,

        @SerialName("video")
        VIDEO,

        @SerialName("gifv")
        GIFV,

        @SerialName("unknown")
        UNKNOWN
    }
}
