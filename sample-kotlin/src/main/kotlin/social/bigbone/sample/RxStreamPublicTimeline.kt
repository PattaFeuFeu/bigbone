@file:Suppress("ktlint:standard:no-multi-spaces", "ktlint:standard:discouraged-comment-location")

package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.entity.streaming.MastodonApiEvent
import social.bigbone.rx.RxStreamingMethods
import java.util.Timer
import kotlin.concurrent.schedule

object RxStreamPublicTimeline {

    @JvmStatic
    fun main(args: Array<String>) {
        val instanceName = args[0]

        // access token with at least Scope.READ.STATUSES
        val accessToken = args[1]

        // require authentication even if public streaming
        val client = MastodonClient.Builder(instanceName)
            .accessToken(accessToken)
            .build()
        val streaming = RxStreamingMethods(client)

        println("init")
        val disposable = streaming.federatedPublic(
            onlyMedia = false
        )
            .filter { it is MastodonApiEvent }
            .map { it as MastodonApiEvent }
            .subscribe(
                { println("Mastodon API event: $it") }, // onNext
                ::println,                              // onError
                { println("onComplete") }               // onComplete
            )

        Timer().schedule(15_000L) {
            disposable.dispose()
        }
    }
}
