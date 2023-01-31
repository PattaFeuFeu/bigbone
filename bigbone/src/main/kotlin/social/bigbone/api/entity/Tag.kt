package social.bigbone.api.entity

import com.google.gson.annotations.SerializedName

/**
 * Represents a hashtag used within the content of a status.
 * @see <a href="https://docs.joinmastodon.org/entities/Tag/">Mastodon API Tag</a>
 */
data class Tag(
    /**
     * The value of the hashtag after the # sign.
     */
    @SerializedName("name")
    val name: String = "",

    /**
     * A link to the hashtag on the instance.
     */
    @SerializedName("url")
    val url: String = ""
)
