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
    val url: String = "",

    /**
     * Usage statistics for given days (typically the past week).
     */
    @SerializedName("history")
    val history: List<History> = emptyList(),

    /**
     * Whether the current token’s authorized user is following this tag.
     */
    @SerializedName("following")
    val following: Boolean? = null
) {
    /**
     * Usage statistics for given days (typically the past week).
     * @see <a href="https://docs.joinmastodon.org/entities/Tag/#history">Mastodon API Tag history</a>
     */
    data class History(

        /**
         * UNIX timestamp on midnight of the given day.
         */
        @SerializedName("day")
        val day: String = "",

        /**
         * The counted usage of the tag within that day, string cast from integer.
         */
        @SerializedName("uses")
        val uses: String = "",

        /**
         * The total of accounts using the tag within that day, string cast from integer.
         */
        @SerializedName("accounts")
        val accounts: String = ""
    )
}
