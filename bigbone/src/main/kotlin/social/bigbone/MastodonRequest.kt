package social.bigbone

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import okhttp3.Response
import social.bigbone.api.entity.Error
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.extension.toPageable

/**
 * Represents an HTTP request that is sent to a Mastodon instance, once the [execute]
 * method on this class is called.
 */
class MastodonRequest<T>(
    private val executor: () -> Response,
    private val mapper: (String) -> Any
) {

    /**
     * SAM interface provided for Java interoperability related to [doOnJson] method.
     */
    interface Action1<T> {
        fun invoke(arg: T)
    }

    private var action: (String) -> Unit = {}

    private var isPageable: Boolean = false

    internal fun toPageable() = apply {
        isPageable = true
    }

    /**
     * Allows you to execute some extra logic (such as logging) that is triggered when the JSON response
     * from the Mastodon API arrives.
     */
    @JvmSynthetic
    fun doOnJson(action: (String) -> Unit) = apply {
        this.action = action
    }

    /**
     * Allows you to execute some extra logic (such as logging) that is triggered when the JSON response
     * from the Mastodon API arrives. This method is provided for Java interoperability reasons.
     */
    fun doOnJson(action: Action1<String>) = apply {
        this.action = { action.invoke(it) }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(BigBoneRequestException::class)
    fun execute(): T {
        val response = executor()
        if (response.isSuccessful) {
            try {
                val body: String = response.body.string()

                val element: JsonElement = JSON_SERIALIZER.parseToJsonElement(body)
                if (element is JsonObject) {
                    action(body)

                    return if (isPageable) {
                        listOf(mapper(body)).toPageable(response) as T
                    } else {
                        mapper(body) as T
                    }
                }

                val mappedJsonElements: List<Any> = element.jsonArray.map { jsonElement: JsonElement ->
                    val json = jsonElement.toString()
                    action(json)

                    mapper(json)
                }

                return if (isPageable) {
                    mappedJsonElements.toPageable(response) as T
                } else {
                    mappedJsonElements as T
                }
            } catch (e: Exception) {
                throw BigBoneRequestException("Successful response could not be parsed", e)
            }
        } else {
            try {
                val error = JSON_SERIALIZER.decodeFromString<Error>(response.body.string())
                response.close()
                throw BigBoneRequestException(response, error)
            } catch (e: IllegalArgumentException) {
                throw BigBoneRequestException("Request failed with unparseable error response", e)
            }
        }
    }
}
