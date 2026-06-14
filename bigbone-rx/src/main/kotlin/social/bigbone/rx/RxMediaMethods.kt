package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.entity.FileAsMediaAttachment
import social.bigbone.api.entity.MediaAttachment
import social.bigbone.api.entity.data.Focus
import social.bigbone.api.method.MediaMethods
import java.io.File

/**
 * Reactive implementation of [MediaMethods].
 * Allows access to API methods with endpoints having an "api/vX/media" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/media/">Mastodon media API methods</a>
 */
class RxMediaMethods(client: MastodonClient) {

    private val mediaMethods = MediaMethods(client)

    /**
     * Creates a [MediaAttachment] to be used with a new status.
     * NOTE: The full-sized media will be processed asynchronously in the background for large uploads.
     *
     * Starting with Mastodon server version 4.0.0:
     * Smaller media formats (image) will be processed synchronously and return 200 instead of 202.
     * Larger media formats (video, gifv, audio) will continue to be processed asynchronously and return 202.
     *
     * Users of this method will not get the raw HTTP status: If this method succeeds, you will get a [MediaAttachment]
     * with either the [MediaAttachment.url] null because the full-sized media has not been processed yet, or non-null
     * because it was small enough to be processes synchronously.
     *
     * Use [getMediaAttachment] for retrieving the status of processing. If its [MediaAttachment.url] is not null,
     * processing is done.
     *
     * @param mediaAttachment The file with media type that should be attached
     * @param description a plain-text description of the media, for accessibility purposes.
     * @param focus a [Focus] instance which specifies the x- and y- coordinate of the focal point. Valid range for x and y is -1.0 to 1.0.
     * @param customThumbnail The custom thumbnail of the media to be attached.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/media/#v2">Mastodon API documentation: methods/media/#v2</a>
     */
    @JvmOverloads
    fun uploadMediaAsync(
        mediaAttachment: FileAsMediaAttachment,
        description: String? = null,
        focus: Focus? = null,
        customThumbnail: FileAsMediaAttachment? = null
    ): Single<MediaAttachment> = Single.fromCallable {
        mediaMethods.uploadMediaAsync(mediaAttachment, description, focus, customThumbnail).execute()
    }

    /**
     * Get a media attachment, before it is attached to a status and posted, but after it is accepted for processing.
     * Use this method to check that the full-size media has finished processing.
     *
     * @param withId The ID of the [MediaAttachment] in the database.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/media/#get">Mastodon API documentation: methods/media/#get</a>
     */
    fun getMediaAttachment(withId: String): Single<MediaAttachment> = Single.fromCallable {
        mediaMethods.getMediaAttachment(withId).execute()
    }

    /**
     * Update a [MediaAttachment]’s parameters before it’s attached to a status and posted.
     *
     * @param withId The ID of the [MediaAttachment] in the database.
     * @param customThumbnail The custom thumbnail of the media to be attached.
     * @param description A plain-text description of the media, for accessibility purposes.
     * @param focus a [Focus] instance which specifies the x- and y-coordinate of the focal point. Valid range for x and y is -1.0 to 1.0.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/media/#update">Mastodon API documentation: methods/media/#update</a>
     */
    @JvmOverloads
    fun updateMediaAttachment(
        withId: String,
        customThumbnail: FileAsMediaAttachment? = null,
        description: String? = null,
        focus: Focus? = null
    ): Single<MediaAttachment> = Single.fromCallable {
        mediaMethods.updateMediaAttachment(withId, customThumbnail, description, focus).execute()
    }

    /**
     * Creates an attachment to be used with a new status. This method will return after the full sized media is done processing.
     *
     * @param file the file that should be uploaded
     * @param mediaType media type of the file as a string, e.g. "image/png"
     * @param description a plain-text description of the media, for accessibility purposes.
     * @param focus a [Focus] instance which specifies the x- and y- coordinate of the focal point. Valid range for x and y is -1.0 to 1.0.
     * @param customThumbnail The custom thumbnail of the media to be attached.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/media/#v1">Mastodon API documentation: methods/media/#v1</a>
     */
    @JvmOverloads
    @Deprecated(
        message = "Use async variant which returns after upload but before media attachment has been processed.",
        replaceWith = ReplaceWith(
            "uploadMediaAsync(FileAsMediaAttachment(file, mediaType), description, focus, customThumbnail)",
            "social.bigbone.api.method.FileAsMediaAttachment"
        )
    )
    fun uploadMedia(
        file: File,
        mediaType: String,
        description: String? = null,
        focus: Focus? = null,
        customThumbnail: FileAsMediaAttachment? = null
    ): Single<MediaAttachment> = Single.fromCallable {
        mediaMethods.uploadMedia(file, mediaType, description, focus, customThumbnail).execute()
    }
}
