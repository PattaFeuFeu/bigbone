package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Filter;
import social.bigbone.api.entity.FilterKeyword;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.Arrays;
import java.util.List;

/**
 * The main method of this class accepts the following parameters:
 * - &lt;instance&gt; &lt;accessToken&gt; list: list all existing filters for this account
 * - &lt;instance&gt; &lt;accessToken&gt; create &lt;keyword&gt; create a new filter for the keyword
 * - &lt;instance&gt; &lt;accessToken&gt; delete &lt;filterId&gt; delete the filter with this filterId
 * - &lt;instance&gt; &lt;accessToken&gt; addKeyword &lt;filterId&gt; &lt;filterId&gt; add keyword to the filter with ID filterId
 */
@SuppressWarnings("PMD.SystemPrintln")
public class ManageFilters {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.FILTERS and Scope.WRITE.FILTERS
        final String accessToken = args[1];

        final String action = args[2];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        switch (action) {
            case "list":
                listExistingFilters(client);
                break;
            case "create":
                createNewFilter(client, args[3]);
                break;
            case "delete":
                deleteFilter(client, args[3]);
                break;
            case "addKeyword":
                addKeywordToFilter(client, args[3], args[4]);
                break;
            default:
                break;
        }
    }

    /**
     * Get a list of filters, then for each filter print out title, ID, filter action and contexts, and its keywords.
     * Similar functionality exists to view an individual filter.
     *
     * @param client a [MastodonClient] with an authenticated user
     */
    private static void listExistingFilters(final MastodonClient client) throws BigBoneRequestException {
        final List<Filter> existingFilters = client.filters().listFilters().execute();
        for (final Filter filter : existingFilters) {
            System.out.println(filter.getTitle() + " (ID " + filter.getId() + "):");
            System.out.print(filter.getFilterAction() + " in the following contexts: ");
            for (final Filter.FilterContext context : filter.getContext()) {
                System.out.print(context + " ");
            }
            System.out.print("\nkeywords: ");
            for (final FilterKeyword filterKeyword : filter.getKeywords()) {
                System.out.print(filterKeyword.getKeyword() + " ");
            }
            System.out.println("\n-------------------------------------------------------");
        }
    }

    /**
     * Creates a new filter for the given keyword. This filter will expire automatically after an hour.
     * Similar functionality exists to update a given filter.
     *
     * @param client          a [MastodonClient] with an authenticated user
     * @param keywordToFilter string that should be filtered by the new filter
     */
    private static void createNewFilter(final MastodonClient client, final String keywordToFilter) throws BigBoneRequestException {
        // title for new filter
        final String title = "BigBone sample filter: " + keywordToFilter;

        // filter context - where do we want statuses to be filtered? (here: in Home and Public timelines)
        final List<Filter.FilterContext> context = Arrays.asList(Filter.FilterContext.HOME, Filter.FilterContext.PUBLIC);

        // create a proper filter keywords list - filters typically contain more than one keyword, and each keyword
        // can be matched either as a whole word or as part of a string (e.g. "@example.org" matching any "user@example.org").
        final FilterKeyword filterKeyword = new FilterKeyword("0", keywordToFilter, true);
        final List<FilterKeyword> keywords = Arrays.asList(filterKeyword);

        // set filter to expire in one hour - this is optional, but done here to not interfere with account used in testing
        final int expiryInSeconds = 3600;

        // filter action - should filtered statuses be shown with a warning, or be hidden completely?
        final Filter.FilterAction action = Filter.FilterAction.WARN;

        // create filter and output its ID
        final Filter createdFilter = client.filters().createFilter(title, context, keywords, expiryInSeconds, action).execute();
        System.out.println("New filter was created with ID " + createdFilter.getId());
    }

    /**
     * Delete a filter with the given filter ID.
     * Similar functionality exists to view a given filter.
     *
     * @param client   a [MastodonClient] with an authenticated user
     * @param filterId ID string for the filter that should be deleted
     */
    private static void deleteFilter(final MastodonClient client, final String filterId) throws BigBoneRequestException {
        client.filters().deleteFilter(filterId);
        System.out.println("Filter was deleted");
    }

    /**
     * Add a keyword to an existing filter.
     * Similar functionality exists to view, delete or update individual keywords, or to list all keywords of a given filter.
     *
     * @param client          a [MastodonClient] with an authenticated user
     * @param filterId        ID string for the filter that should be edited
     * @param keywordToFilter string for a new keyword that should be filtered by the filter
     */
    private static void addKeywordToFilter(final MastodonClient client, final String filterId, final String keywordToFilter) throws BigBoneRequestException {
        // create a proper filter keyword
        final FilterKeyword localFilterKeyword = new FilterKeyword("0", keywordToFilter, true);

        // add keyword to filter
        final FilterKeyword newFilterKeyword = client.filters().addKeyword(filterId, localFilterKeyword).execute();
        System.out.println("Keyword \"" + keywordToFilter + "\" was added to filter with ID " + newFilterKeyword.getId());
    }
}
