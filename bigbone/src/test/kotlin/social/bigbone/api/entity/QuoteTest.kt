package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.testtool.AssetsUtil

class QuoteTest {

    @Test
    fun deserializeAcceptedQuote() {
        val json = AssetsUtil.readFromAssets("quote_post_accepted.json")
        val quote: Quote = JSON_SERIALIZER.decodeFromString(json)
        quote.state shouldBeEqualTo Quote.State.ACCEPTED
        quote.quotedStatus shouldNotBe null
        quote.quotedStatus?.id shouldBeEqualTo "103270115826048975"
        quote.quotedStatus?.account?.id shouldBeEqualTo "1"
    }

    @Test
    fun deserializeRejectedQuote() {
        val json = "{\"state\": \"rejected\"}"
        val quote: Quote = JSON_SERIALIZER.decodeFromString(json)
        quote.state shouldBeEqualTo Quote.State.REJECTED
        quote.quotedStatus shouldBeEqualTo null
    }

    @Test
    fun deserializeBlockedAccountQuote() {
        val json = "{\"state\": \"blocked_account\"}"
        val quote: Quote = JSON_SERIALIZER.decodeFromString(json)
        quote.state shouldBeEqualTo Quote.State.BLOCKED_ACCOUNT
        quote.quotedStatus shouldBeEqualTo null
    }
}
