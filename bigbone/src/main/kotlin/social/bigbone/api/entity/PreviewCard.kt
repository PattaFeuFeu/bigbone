package social.bigbone.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.bigbone.api.entity.PreviewCard.CardType

/**
 * Represents a rich preview card that is generated using OpenGraph tags from a URL.
 * @see <a href="https://docs.joinmastodon.org/entities/PreviewCard/">Mastodon API PreviewCard</a>
 */
@Serializable
data class PreviewCard(
    /**
     * Location of linked resource.
     */
    @SerialName("url")
    val url: String = "",

    /**
     * Title of linked resource.
     */
    @SerialName("title")
    val title: String = "",

    /**
     * Description of preview.
     */
    @SerialName("description")
    val description: String = "",

    /**
     * The type of the preview card.
     * @see CardType
     */
    @SerialName("type")
    val type: CardType = CardType.LINK,

    /**
     * The author of the original resource.
     */
    @SerialName("author_name")
    val authorName: String = "",

    /**
     * A link to the author of the original resource.
     */
    @SerialName("author_url")
    val authorUrl: String = "",

    /**
     * The provider of the original resource.
     */
    @SerialName("provider_name")
    val providerName: String = "",

    /**
     * A link to the provider of the original resource.
     */
    @SerialName("provider_url")
    val providerUrl: String = "",

    /**
     * HTML to be used for generating the preview card.
     */
    @SerialName("html")
    val html: String = "",

    /**
     * Width of preview, in pixels.
     */
    @SerialName("width")
    val width: Int = 0,

    /**
     * Height of preview, in pixels.
     */
    @SerialName("height")
    val height: Int = 0,

    /**
     * Preview thumbnail.
     */
    @SerialName("image")
    val image: String? = null,

    /**
     * Used for photo embeds, instead of custom html.
     */
    @SerialName("embed_url")
    val embedUrl: String = "",

    /**
     * A hash computed by the BlurHash algorithm,
     * for generating colorful preview thumbnails
     * when media has not been downloaded yet.
     */
    @SerialName("blurhash")
    val blurhash: String? = null
) {
    /**
     * Specifies the type of the preview card.
     */
    @Serializable
    enum class CardType {
        @SerialName("link")
        LINK,

        @SerialName("photo")
        PHOTO,

        @SerialName("video")
        VIDEO,

        @SerialName("rich")
        RICH
    }
}
