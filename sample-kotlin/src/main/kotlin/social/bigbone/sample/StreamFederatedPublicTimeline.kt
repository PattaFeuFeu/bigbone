package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.entity.streaming.MastodonApiEvent
import social.bigbone.api.entity.streaming.MastodonApiEvent.GenericMessage
import social.bigbone.api.entity.streaming.MastodonApiEvent.StreamEvent
import social.bigbone.api.entity.streaming.TechnicalEvent

object StreamFederatedPublicTimeline {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.READ.STATUSES
        val accessToken = args[1]

        // Instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        client.streaming.federatedPublic(
            onlyMedia = false,
            callback = {
                when (it) {
                    is TechnicalEvent -> println("Technical event: $it")
                    is MastodonApiEvent -> when (it) {
                        is GenericMessage -> println("Generic message: $it")
                        is StreamEvent -> println("API event: ${it.event::class.java.simpleName}")
                    }
                }
            }
        )
    }
}
