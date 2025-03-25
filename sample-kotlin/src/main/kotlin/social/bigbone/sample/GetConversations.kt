package social.bigbone.sample

import social.bigbone.MastodonClient

object GetConversations {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.READ.STATUSES
        val accessToken = args[1]

        // instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        // Get conversations
        val conversations = client.conversations.getConversations().execute()
        conversations.part.forEach { conversation ->
            val lastStatusText = conversation.lastStatus?.content
            print("$conversation.id $lastStatusText")
        }
    }
}
