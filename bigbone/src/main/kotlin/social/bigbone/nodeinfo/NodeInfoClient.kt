package social.bigbone.nodeinfo

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import social.bigbone.JSON_SERIALIZER
import social.bigbone.api.exception.ServerInfoRetrievalException
import social.bigbone.api.exception.ServerInfoUrlRetrievalException
import social.bigbone.nodeinfo.entity.NodeInfo
import social.bigbone.nodeinfo.entity.Server

/**
 * Client to retrieve information about a server via NodeInfo, given its hostname.
 * @see <a href="https://github.com/jhass/nodeinfo">NodeInfo information</a>
 */
object NodeInfoClient {

    private val CLIENT = OkHttpClient.Builder()
        .followRedirects(true)
        .build()

    /**
     * Retrieve server information.
     * @param host hostname of the server to retrieve information from
     * @return server information, including the name and version of the software running on this server
     * @throws ServerInfoRetrievalException if the request for a server info to [host] was unsuccessful
     */
    @Throws(ServerInfoRetrievalException::class)
    fun retrieveServerInfo(host: String): Server? {
        CLIENT.newCall(
            Request.Builder()
                .url(getServerInfoUrl(host))
                .get()
                .build()
        ).execute().use { response: Response ->
            if (!response.isSuccessful) {
                throw ServerInfoRetrievalException(
                    message = "request for NodeInfo URL unsuccessful",
                    response = response
                )
            }

            return response.body?.string()?.let { JSON_SERIALIZER.decodeFromString(it) }
        }
    }

    /**
     * Get the URL to retrieve server information from.
     * @param host the hostname of the server to request information from
     * @return String containing the URL holding server information
     * @throws ServerInfoUrlRetrievalException if we could not call the [host] or if the [NodeInfo] was empty
     */
    @Throws(ServerInfoUrlRetrievalException::class)
    private fun getServerInfoUrl(host: String): String {
        CLIENT.newCall(
            Request.Builder()
                .url("https://$host/.well-known/nodeinfo")
                .get()
                .build()
        ).execute().use { response: Response ->
            if (!response.isSuccessful) {
                throw ServerInfoUrlRetrievalException(
                    message = "request for well-known NodeInfo URL unsuccessful",
                    response = response
                )
            }

            val nodeInfo: NodeInfo? = response.body?.string()?.let { JSON_SERIALIZER.decodeFromString(it) }
            if (nodeInfo == null || nodeInfo.links.isEmpty()) {
                throw ServerInfoUrlRetrievalException(
                    message = "empty link list in well-known NodeInfo location",
                    response = response
                )
            }

            // attempt returning URL to schema 2.0 information, but fall back to any - software information exists in all schemas
            return nodeInfo.links
                .firstOrNull { link -> link.rel == "http://nodeinfo.diaspora.software/ns/schema/2.0" }
                ?.href
                ?: nodeInfo.links.first().href
        }
    }
}
