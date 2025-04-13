package social.bigbone.api.exception

import okhttp3.Response
import social.bigbone.MastodonClient
import social.bigbone.nodeinfo.entity.NodeInfo

/**
 * Exception thrown if we could not instantiate a [MastodonClient]. Mostly used to wrap other more specific exceptions.
 */
open class BigBoneClientInstantiationException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception thrown if we could not retrieve server information during [MastodonClient] instantiation.
 */
class ServerInfoRetrievalException : BigBoneClientInstantiationException {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(response: Response, message: String? = null) : super(
        message = "${message ?: ""}${response.message}"
    )
}

/**
 * Exception thrown if we could not successfully get the [NodeInfo] server URL during [MastodonClient] instantiation.
 */
class ServerInfoUrlRetrievalException(
    response: Response,
    message: String? = null
) : BigBoneClientInstantiationException(message = "${message ?: ""}${response.message}")

/**
 * Exception thrown if the server does not support the required features during [MastodonClient] instantiation.
 */
class UnsupportedServerException(message: String, cause: Throwable? = null) :
    BigBoneClientInstantiationException(message, cause)

/**
 * Exception thrown if we could not retrieve the instance details of a Mastodon server during [MastodonClient] instantiation.
 */
class InstanceRetrievalException(message: String, cause: Throwable? = null) :
    BigBoneClientInstantiationException(message, cause)
