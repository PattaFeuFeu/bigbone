package social.bigbone.api.entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Quote policy for new posts.
 */
@Serializable
enum class QuotePolicy {

    /**
     * New posts may be quoted by anyone.
     */
    @SerialName("public")
    PUBLIC,

    /**
     * New posts may be quoted only by followers.
     */
    @SerialName("followers")
    FOLLOWERS,

    /**
     * New posts may be quoted by nobody.
     */
    @SerialName("nobody")
    NOBODY;

    @OptIn(ExperimentalSerializationApi::class)
    val apiName: String get() = serializer().descriptor.getElementName(ordinal)
}
