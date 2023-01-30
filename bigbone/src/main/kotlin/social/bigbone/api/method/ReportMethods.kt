package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.entity.Report
import social.bigbone.api.exception.BigBoneRequestException

/**
 * Allows access to API methods with endpoints having an "api/vX/reports" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/reports/">Mastodon reports API methods</a>
 */
class ReportMethods(private val client: MastodonClient) {
    /**
     * File a report.
     * @param accountId The ID of the account to report
     * @param statusIds List of ID strings for statuses to be reported
     * @param comment The reason for the report. Default maximum of 1000 characters.
     * @see <a href="https://docs.joinmastodon.org/methods/reports/#post">Mastodon API documentation: methods/reports/#post</a>
     */
    @Throws(BigBoneRequestException::class)
    fun fileReport(accountId: String, statusIds: List<String>, comment: String): MastodonRequest<Report> {
        return client.getMastodonRequest(
            endpoint = "api/v1/reports",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("account_id", accountId)
                append("status_ids", statusIds)
                append("comment", comment)
            }
        )
    }
}
