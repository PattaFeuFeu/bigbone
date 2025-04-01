package social.bigbone.api.method

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import social.bigbone.PrecisionDateTime
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.testtool.MockClient

class MuteMethodsTest {
    @Test
    fun getMutes() {
        val client = MockClient.mock("mutes.json")
        val muteMethods = MuteMethods(client)
        val pageable = muteMethods.getMutes().execute()
        val account = pageable.part.first()
        account.id shouldBeEqualTo "11111"
        account.acct shouldBeEqualTo "test@test.com"
        account.displayName shouldBeEqualTo "test"
        account.username shouldBeEqualTo "test"
        account.muteExpiresAt shouldBeInstanceOf PrecisionDateTime.ValidPrecisionDateTime.ExactTime::class

        val accountWithExplicitNullValue = pageable.part[1]
        accountWithExplicitNullValue.id shouldBeEqualTo "22222"
        accountWithExplicitNullValue.muteExpiresAt shouldBeInstanceOf PrecisionDateTime.InvalidPrecisionDateTime.Unavailable::class

        val accountWithImplicitNullValue = pageable.part[2]
        accountWithImplicitNullValue.id shouldBeEqualTo "33333"
        accountWithImplicitNullValue.muteExpiresAt shouldBeInstanceOf PrecisionDateTime.InvalidPrecisionDateTime.Unavailable::class
    }

    @Test
    fun getMutesWithException() {
        Assertions.assertThrows(BigBoneRequestException::class.java) {
            val client = MockClient.ioException()
            val muteMethods = MuteMethods(client)
            muteMethods.getMutes().execute()
        }
    }
}
