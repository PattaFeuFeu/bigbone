package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.entity.Filter

object GetHomeTimelineWithFiltering {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.READ.STATUSES
        val accessToken = args[1]

        // Instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        // Get statuses from home timeline, then handle each one
        val statuses = client.timelines.getHomeTimeline().execute()
        statuses.part.forEach { status ->

            // determine if we need to hide the status or just warn about it
            var shouldWarn = false
            var shouldHide = false
            status.filtered?.let { filterResultList ->
                filterResultList.forEach {
                    val filter = it.filter

                    // only use a filter if it applies to our current context (in this case, the home timeline)
                    if (filter.context.contains(Filter.FilterContext.HOME)) {
                        // check the filter action set for this filter
                        when (filter.filterAction) {
                            Filter.FilterAction.WARN -> shouldWarn = true
                            Filter.FilterAction.HIDE -> shouldHide = true
                        }
                    }
                }
            }

            // display the status as is appropriate
            if (!shouldHide) {
                // hidden statuses are skipped completely

                // display warning, or post content if no filters matched
                if (shouldWarn) {
                    println("********************************************************************************")
                    println("* WARNING * status by ${status.account?.displayName} matches one or more of your filters")
                    println("********************************************************************************")
                } else {
                    println("${status.account?.displayName} posted: ${status.content.take(50)}")
                }
            }
        }
    }
}
