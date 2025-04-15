package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.testtool.AssetsUtil

class FilterTest {

    @Test
    fun deserialize() {
        // given a JSON string containing valid Filter data
        val json = AssetsUtil.readFromAssets("filter.json")

        // when parsing as a Filter
        val filter: Filter = JSON_SERIALIZER.decodeFromString(json)

        // then this Filter should have all the available information
        filter.id shouldBeEqualTo "20060"
        filter.filterAction shouldBeEqualTo Filter.FilterAction.HIDE
        filter.context.contains(Filter.FilterContext.PUBLIC) shouldBeEqualTo true
        filter.keywords.size shouldBeEqualTo 3
        filter.keywords.first().keyword shouldBeEqualTo "from birdsite"
    }

    @Test
    fun deserializeWithUnknownAction() {
        // given a JSON string containing valid Filter data, but with an unknown filter_action
        val json = AssetsUtil.readFromAssets("filter_unknown_action.json")

        // when parsing as a Filter
        val filter: Filter = JSON_SERIALIZER.decodeFromString(json)

        // then this Filter's action should fall back to FilterAction.WARN
        filter.filterAction shouldBeEqualTo Filter.FilterAction.WARN
    }
}
