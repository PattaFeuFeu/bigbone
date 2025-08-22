package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.entity.Quote

object GetQuotedStatus {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.READ.STATUSES
        val accessToken = args[1]

        // ID value of a status that should be retrieved and checked for a quoted status
        val statusId = args[2]

        // instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        // get the status
        val status = client.statuses.getStatus(statusId).execute()

        // status may not have a quoted status
        if (status.quote == null) {
            println("Status with ID $statusId does not contain a quoted status")
        } else {
            // if it has, quote might not have been accepted by the original author
            if (status.quote?.state != Quote.State.ACCEPTED) {
                println("Status with ID $statusId contains a quoted status in a non-accepted state")
            }

            // if the quote is accepted...
            if (status.quote?.state == Quote.State.ACCEPTED) {
                // ...the quoted status might be contained directly
                status.quote?.quotedStatus?.let {
                    println(
                        "Status with ID $statusId contains a quoted status with the following text content, " +
                            "which was returned directly:\n ${it.content} (${it.id})"
                    )
                }

                // ...or might need to be retrieved via its ID
                status.quote?.quotedStatusId?.let {
                    val quotedStatus = client.statuses.getStatus(it).execute()
                    println("Quoted status has the following text content, retrieved via its ID value:\n ${quotedStatus.content}")
                }
            }
        }
    }
}
