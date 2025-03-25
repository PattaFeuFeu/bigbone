package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Filter;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.concurrent.atomic.AtomicBoolean;

public class GetHomeTimelineWithFiltering {

    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.STATUSES
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Get statuses from home timeline, then handle each one
        final Pageable<Status> statuses = client.timelines().getHomeTimeline().execute();
        statuses.getPart().forEach(status -> {

            // determine if we need to hide the status or just warn about it
            AtomicBoolean shouldWarn = new AtomicBoolean(false);
            AtomicBoolean shouldHide = new AtomicBoolean(false);
            if (status.getFiltered() != null) {
                status.getFiltered().forEach(filterResult -> {
                    Filter filter = filterResult.getFilter();

                    // only use a filter if it applies to our current context (in this case, the home timeline)
                    if (filter.getContext().contains(Filter.FilterContext.HOME)) {

                        // check the filter action set for this filter
                        if (filter.getFilterAction().equals(Filter.FilterAction.WARN)) {
                            shouldWarn.set(true);
                        }
                        if (filter.getFilterAction().equals(Filter.FilterAction.HIDE)) {
                            shouldHide.set(true);
                        }

                    }
                });
            }

            // display the status as is appropriate
            if (!shouldHide.get()) {
                // hidden statuses are skipped completely

                // display warning, or post content if no filters matched
                String name = "someone";
                if (status.getAccount() != null) {
                    name = status.getAccount().getDisplayName();
                }
                if (shouldWarn.get()) {
                    System.out.println("********************************************************************************");
                    System.out.println("* WARNING * status by " + name + " matches one or more of your filters");
                    System.out.println("********************************************************************************");
                } else {
                    final String content = status.getContent();
                    System.out.println(name + " posted: " + content);
                }
            }
        });
    }
}
