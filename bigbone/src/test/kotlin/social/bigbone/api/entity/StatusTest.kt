package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.PrecisionDateTime
import social.bigbone.api.entity.data.Visibility
import social.bigbone.testtool.AssetsUtil

class StatusTest {

    @Test
    fun deserialize() {
        val json = AssetsUtil.readFromAssets("status.json")
        val status: Status = JSON_SERIALIZER.decodeFromString(json)
        status.id shouldBeEqualTo "11111"
        status.visibility shouldBeEqualTo Visibility.PUBLIC
        status.content shouldBeEqualTo "Test Status"
        val account = status.account
        requireNotNull(account)
        account.id shouldBeEqualTo "14476"
        status.isReblogged shouldBeEqualTo false
    }

    @Test
    fun deserializeStatusWithQuote() {
        val json = AssetsUtil.readFromAssets("status_with_quote.json")
        val status: Status = JSON_SERIALIZER.decodeFromString(json)
        status.quote?.state shouldBeEqualTo Quote.State.ACCEPTED
        status.quote?.quotedStatus?.account?.id shouldBeEqualTo "14477"
        status.quote?.quotedStatus?.id shouldBeEqualTo "11112"
        status.quote?.quotedStatusId shouldBeEqualTo null
    }

    @Test
    fun deserializeStatusWithShallowQuote() {
        val json = AssetsUtil.readFromAssets("status_with_shallow_quote.json")
        val status: Status = JSON_SERIALIZER.decodeFromString(json)
        status.quote?.state shouldBeEqualTo Quote.State.ACCEPTED
        status.quote?.quotedStatus shouldBeEqualTo null
        status.quote?.quotedStatusId shouldBeEqualTo "999999"
    }

    @Test
    fun deserializeStatusWithRevokedQuote() {
        val json = AssetsUtil.readFromAssets("status_with_revoked_quote.json")
        val status: Status = JSON_SERIALIZER.decodeFromString(json)
        status.quote?.state shouldBeEqualTo Quote.State.REVOKED
        status.quote?.quotedStatus shouldBeEqualTo null
        status.quote?.quotedStatusId shouldBeEqualTo null
    }

    @Test
    fun constructor() {
        val status = Status(id = "123", visibility = Visibility.PRIVATE)
        status.id shouldBeEqualTo "123"
        status.visibility shouldBeEqualTo Visibility.PRIVATE
        status.content shouldBeEqualTo ""
        status.createdAt shouldBeEqualTo PrecisionDateTime.InvalidPrecisionDateTime.Unavailable
    }
}
