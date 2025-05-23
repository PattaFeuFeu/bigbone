package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.entity.Filter
import social.bigbone.api.entity.FilterKeyword
import social.bigbone.api.entity.FilterStatus
import social.bigbone.api.exception.BigBoneRequestException

/**
 * Allows access to API methods with endpoints having an "api/vX/filters" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/filters/">Mastodon filters API methods</a>
 */
class FilterMethods(private val client: MastodonClient) {

    private val endpoint = "api/v2/filters"

    /**
     * Obtain a list of all filter groups for the current user.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#get">Mastodon API documentation: methods/filters/#get</a>
     */
    @Throws(BigBoneRequestException::class)
    fun listFilters(): MastodonRequest<List<Filter>> {
        return client.getMastodonRequestForList(
            endpoint = endpoint,
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Obtain a single filter group owned by the current user.
     * @param filterId the ID of the Filter in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#get-one">Mastodon API documentation: methods/filters/#get-one</a>
     */
    @Throws(BigBoneRequestException::class)
    fun viewFilter(filterId: String): MastodonRequest<Filter> {
        return client.getMastodonRequest(
            endpoint = "api/v2/filters/$filterId",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Create a filter group with the given parameters.
     * @param title the name of the filter group
     * @param context where the filter should be applied. Specify at least one of home, notifications, public, thread, account.
     * @param filterKeywords list of [FilterKeyword] to be added to the newly-created filter group
     * @param expiresIn how many seconds from now should the filter expire?
     * @param filterAction the policy to be applied when the filter is matched. Note that [Filter.FilterAction.BLUR]
     *  can not be used when attempting to create a filter on a Mastodon instance running API version < 5.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#create">Mastodon API documentation: methods/filters/#create</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun createFilter(
        title: String,
        context: List<Filter.FilterContext>,
        filterKeywords: List<FilterKeyword>,
        expiresIn: Int? = null,
        filterAction: Filter.FilterAction = Filter.FilterAction.WARN
    ): MastodonRequest<Filter> {
        // see: https://github.com/mastodon/mastodon/pull/34256
        if (client.performCompatibilityChecks && filterAction == Filter.FilterAction.BLUR && client.getInstance().apiVersions.mastodon < 5) {
            throw BigBoneRequestException("FilterAction.BLUR not available on Mastodon instance with API version < 5")
        }

        return client.getMastodonRequest(
            endpoint = endpoint,
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("title", title)
                for (c in context) {
                    append("context[]", c.name.lowercase())
                }
                append("filter_action", filterAction.name.lowercase())
                expiresIn?.let {
                    append("expires_in", it)
                }
                for (filterKeyword in filterKeywords) {
                    append("keywords_attributes[][keyword]", filterKeyword.keyword)
                    append("keywords_attributes[][whole_word]", filterKeyword.wholeWord)
                }
            }
        )
    }

    /**
     * Update a filter group with the given parameters.
     * @param filterId the ID of the Filter in the database.
     * @param title the name of the filter group
     * @param context where the filter should be applied. Specify at least one of home, notifications, public, thread, account.
     * @param filterAction the policy to be applied when the filter is matched. Specify warn or hide.
     * @param expiresIn how many seconds from now should the filter expire?
     * @param keywordsToAdd list of [FilterKeyword] that will be added to this filter group;
     *   ID values of these FilterKeywords will be ignored
     * @param keywordsToUpdate list of [FilterKeyword] that will be updated in this filter group;
     *   ID values of these FilterKeywords should already exist in the filter group
     * @param keywordsToRemove list of [FilterKeyword] that will be removed from this filter group;
     *   only the ID values of these FilterKeywords will be used, and should already exist in the filter group
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#update">Mastodon API documentation: methods/filters/#update</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun updateFilter(
        filterId: String,
        title: String? = null,
        context: List<Filter.FilterContext>? = null,
        filterAction: Filter.FilterAction? = null,
        expiresIn: Int? = null,
        keywordsToAdd: List<FilterKeyword>? = null,
        keywordsToUpdate: List<FilterKeyword>? = null,
        keywordsToRemove: List<FilterKeyword>? = null
    ): MastodonRequest<Filter> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/$filterId",
            method = MastodonClient.Method.PUT,
            parameters = Parameters().apply {
                title?.let {
                    append("title", it)
                }
                context?.let {
                    for (c in it) {
                        append("context[]", c.name.lowercase())
                    }
                }
                filterAction?.let {
                    append("filter_action", it.name.lowercase())
                }
                expiresIn?.let {
                    append("expires_in", it)
                }
                keywordsToAdd?.let {
                    for (filterKeyword in it) {
                        // append FilterKeyword values to create new keyword
                        append("keywords_attributes[][keyword]", filterKeyword.keyword)
                        append("keywords_attributes[][whole_word]", filterKeyword.wholeWord)
                    }
                }
                keywordsToUpdate?.let {
                    for (filterKeyword in it) {
                        append("keywords_attributes[][id]", filterKeyword.id)
                        append("keywords_attributes[][keyword]", filterKeyword.keyword)
                        append("keywords_attributes[][whole_word]", filterKeyword.wholeWord)
                    }
                }
                keywordsToRemove?.let {
                    for (filterKeyword in it) {
                        append("keywords_attributes[][id]", filterKeyword.id)
                        append("keywords_attributes[][_destroy]", true)
                    }
                }
            }
        )
    }

    /**
     * Delete a filter group with the given id.
     * @param filterId the ID of the Filter in the database
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#delete">Mastodon API documentation: methods/filters/#delete</a>
     */
    @Throws(BigBoneRequestException::class)
    fun deleteFilter(filterId: String) {
        client.performAction(
            endpoint = "$endpoint/$filterId",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * List all keywords attached to the current filter group.
     * @param filterId the ID of the Filter in the database
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#keywords-get">Mastodon API documentation: methods/filters/#keywords-get</a>
     */
    @Throws(BigBoneRequestException::class)
    fun listKeywords(filterId: String): MastodonRequest<List<FilterKeyword>> {
        return client.getMastodonRequestForList(
            endpoint = "$endpoint/$filterId/keywords",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Add the given keyword to the specified filter group.
     * @param filterId The ID of the Filter in the database.
     * @param filterKeyword The keyword to be added to the filter group.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#keywords-create">Mastodon API documentation: methods/filters/#keywords-create</a>
     */
    @Throws(BigBoneRequestException::class)
    fun addKeyword(filterId: String, filterKeyword: FilterKeyword): MastodonRequest<FilterKeyword> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/$filterId/keywords",
            method = MastodonClient.Method.POST,
            Parameters().apply {
                append("keyword", filterKeyword.keyword)
                append("whole_word", filterKeyword.wholeWord)
            }
        )
    }

    /**
     * Get one filter keyword by the given ID.
     * @param keywordId The ID of the FilterKeyword in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#keywords-get-one">Mastodon API documentation: methods/filters/#keywords-get-one</a>
     */
    @Throws(BigBoneRequestException::class)
    fun viewKeyword(keywordId: String): MastodonRequest<FilterKeyword> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/keywords/$keywordId",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Update the given filter keyword.
     * @param filterKeyword The keyword to be updated, including a valid ID.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#keywords-update">Mastodon API documentation: methods/filters/#keywords-update</a>
     */
    @Throws(BigBoneRequestException::class)
    fun updateKeyword(filterKeyword: FilterKeyword): MastodonRequest<FilterKeyword> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/keywords/${filterKeyword.id}",
            method = MastodonClient.Method.PUT,
            Parameters().apply {
                append("keyword", filterKeyword.keyword)
                append("whole_word", filterKeyword.wholeWord)
            }
        )
    }

    /**
     * Deletes the given filter keyword.
     * @param keywordId The ID of the FilterKeyword in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#keywords-delete">Mastodon API documentation: methods/filters/#keywords-delete</a>
     */
    @Throws(BigBoneRequestException::class)
    fun deleteKeyword(keywordId: String) {
        client.performAction(
            endpoint = "$endpoint/keywords/$keywordId",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * Obtain a list of all status filters within this filter group.
     * @param filterId The ID of the Filter in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#statuses-get">Mastodon API documentation: methods/filters/#statuses-get</a>
     */
    @Throws(BigBoneRequestException::class)
    fun listStatusFilters(filterId: String): MastodonRequest<List<FilterStatus>> {
        return client.getMastodonRequestForList(
            endpoint = "$endpoint/$filterId/statuses",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Add a status filter to the current filter group.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#statuses-add">Mastodon API documentation: methods/filters/#statuses-add</a>
     */
    @Throws(BigBoneRequestException::class)
    fun addStatusToFilter(filterId: String, statusId: String): MastodonRequest<FilterStatus> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/$filterId/statuses",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("status_id", statusId)
            }
        )
    }

    /**
     * Obtain a single status filter.
     * @param filterStatusId The ID of the FilterStatus in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#statuses-get-one">Mastodon API documentation: methods/filters/#statuses-get-one</a>
     */
    @Throws(BigBoneRequestException::class)
    fun viewStatusFilter(filterStatusId: String): MastodonRequest<FilterStatus> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/statuses/$filterStatusId",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Remove a status from a filter group.
     * @param filterStatusId The ID of the FilterStatus in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/filters/#statuses-remove">Mastodon API documentation: methods/filters/#statuses-remove</a>
     */
    @Throws(BigBoneRequestException::class)
    fun removeStatusFromFilter(filterStatusId: String): MastodonRequest<FilterStatus> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/statuses/$filterStatusId",
            method = MastodonClient.Method.DELETE
        )
    }
}
