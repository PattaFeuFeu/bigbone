package social.bigbone

import kotlinx.serialization.Serializable

/**
 * Represents the dimensions of an image or video.
 */
@Serializable(with = DimensionSerializer::class)
data class Dimension(
    val width: Int,
    val height: Int
) {
    override fun toString(): String = "${width}x$height"
}
