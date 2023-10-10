package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.entity.Search

/**
 * Allows access to API methods with endpoints having an "api/vX/search" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/search/">Mastodon search API methods</a>
 */
class SearchMethods(private val client: MastodonClient) {

    /**
     * Specify whether to search for only accounts, hashtags, statuses.
     */
    enum class SearchType(val type: String) {
        ACCOUNTS("accounts"),
        HASHTAGS("hashtags"),
        STATUSES("statuses")
    }

    /**
     * Search for content in accounts, statuses and hashtags.
     * @param query string to search for
     * @param type to specify if you are looking for a specific typology
     * @param resolve whether to resolve non-local accounts
     * @param following whether to include accounts that the user is following
     * @param excludeUnreviewed whether to look for trending tags
     * @param accountId to only return statuses authored by the user account
     * @param maxId to return results whose id will be lesser than this one
     * @param minId to return results whose id will be newer than this one
     * @param limit to limit number of results within a range of 20-40 per category
     * @param offset to skip the first n results
     * @see <a href="https://docs.joinmastodon.org/methods/search/">Mastodon API documentation: methods/search</a>
     */
    @JvmOverloads
    fun searchContent(
            query: String,
            type: SearchType? = null,
            resolve: Boolean = false,
            following: Boolean = false,
            excludeUnreviewed: Boolean = false,
            accountId: String? = null,
            maxId: String? = null,
            minId: String? = null,
            limit: Int? = null,
            offset: Int? = null
            ): MastodonRequest<Search> {
        return client.getMastodonRequest(
            endpoint = "api/v2/search",
            method = MastodonClient.Method.GET,
            parameters = buildParameters(query, type, resolve, following, excludeUnreviewed, accountId, maxId, minId, limit, offset)
        )
    }

    private fun buildParameters(
        query: String,
        type: SearchType? = null,
        resolve: Boolean = false,
        following: Boolean = false,
        excludeUnreviewed: Boolean = false,
        accountId: String? = null,
        maxId: String? = null,
        minId: String? = null,
        limit: Int? = null,
        offset: Int? = null
    ): Parameters {
            return Parameters().apply {
                append("q", query)
                append("offset", offset ?: 0)
                if (resolve) {
                    append("resolve", true)
                }
                if (following) {
                    append("following", true)
                }
                if (excludeUnreviewed) {
                    append("exclude_unreviewed", true)
                }
                if (type != null) {
                    append("type", type.name)
                }
                if (!accountId.isNullOrEmpty() && accountId.isNotBlank()) {
                    append("account_id", accountId)
                }
                if (!maxId.isNullOrEmpty() && maxId.isNotBlank()) {
                    append("max_id", maxId)
                }
                if (!minId.isNullOrEmpty() && minId.isNotBlank()) {
                    append("min_id", minId)
                }
                if (limit != null) {
                    append("limit", limit.coerceIn(20, 40))
                }
            }
    }
}
