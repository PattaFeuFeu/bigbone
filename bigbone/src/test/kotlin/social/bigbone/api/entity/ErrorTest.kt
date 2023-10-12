package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.testtool.AssetsUtil

class ErrorTest {

    @Test
    fun deserialize() {
        val json = AssetsUtil.readFromAssets("error.json")
        val error: Error = JSON_SERIALIZER.decodeFromString(json)
        error.error shouldBeEqualTo "invalid_grant"
        error.errorDescription shouldBeEqualTo "Here comes the more detailed error description."
    }

    @Test
    fun constructor() {
        val error = Error(error = "invalid_grant", errorDescription = "Here comes the more detailed error description.")
        error.error shouldBeEqualTo "invalid_grant"
        error.errorDescription shouldBeEqualTo "Here comes the more detailed error description."
    }

    @Test
    fun deserialize_no_description() {
        val json = AssetsUtil.readFromAssets("error_no_description.json")
        val error: Error = JSON_SERIALIZER.decodeFromString(json)
        error.error shouldBeEqualTo "invalid_grant"
        error.errorDescription.shouldBeNull()
    }

    @Test
    fun constructor_no_description() {
        val error = Error(error = "invalid_grant")
        error.error shouldBeEqualTo "invalid_grant"
        error.errorDescription.shouldBeNull()
    }
}
