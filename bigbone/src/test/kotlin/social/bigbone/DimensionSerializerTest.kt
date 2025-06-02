package social.bigbone

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DimensionSerializerTest {

    @Test
    fun `Given a valid dimension string, when decoding from string, then return Dimension with correct values`() {
        val jsonString = "\"800x600\""

        val decodedDimension: Dimension = JSON_SERIALIZER.decodeFromString(jsonString)

        decodedDimension.width shouldBeEqualTo 800
        decodedDimension.height shouldBeEqualTo 600
    }

    @Test
    fun `Given a Dimension object, when encoding into JSON string, then return expected dimension string`() {
        val dimension = Dimension(1920, 1080)

        val encodedDimension = JSON_SERIALIZER.encodeToString(dimension)

        encodedDimension shouldBeEqualTo "\"1920x1080\""
    }

    @Test
    fun `Given a Dimension object, when encoding and then decoding, then return the original Dimension object`() {
        val dimension = Dimension(640, 480)

        val encodedDimension: String = JSON_SERIALIZER.encodeToString(dimension)
        val decodedDimension: Dimension = JSON_SERIALIZER.decodeFromString(encodedDimension)

        decodedDimension shouldBeEqualTo dimension
    }

    @Test
    fun `Given a dimension string without x separator, when decoding from string, then throw IllegalArgumentException`() {
        val jsonString = "\"800600\""

        val exception = assertThrows<IllegalArgumentException> {
            JSON_SERIALIZER.decodeFromString<Dimension>(jsonString)
        }
        exception.message shouldBeEqualTo "Invalid dimension format: 800600. Expected format: widthxheight"
        exception.cause shouldBe null
    }

    @Test
    fun `Given a dimension string with non-integer width, when decoding from string, then throw IllegalArgumentException`() {
        val jsonString = "\"abcx600\""

        val exception = assertThrows<IllegalArgumentException> {
            JSON_SERIALIZER.decodeFromString<Dimension>(jsonString)
        }
        exception.message shouldBeEqualTo "Invalid dimension values in: abcx600. Width and height must be integers."
        exception.cause shouldBeInstanceOf NumberFormatException::class
    }

    @Test
    fun `Given a dimension string with non-integer height, when decoding from string, then throw IllegalArgumentException`() {
        val jsonString = "\"800xabc\""

        val exception = assertThrows<IllegalArgumentException> {
            JSON_SERIALIZER.decodeFromString<Dimension>(jsonString)
        }
        exception.message shouldBeEqualTo "Invalid dimension values in: 800xabc. Width and height must be integers."
        exception.cause shouldBeInstanceOf NumberFormatException::class
    }

    @Test
    fun `Given a dimension string with too many x separators, when decoding from string, then throw IllegalArgumentException`() {
        val jsonString = "\"800x600x400\""

        val exception = assertThrows<IllegalArgumentException> {
            JSON_SERIALIZER.decodeFromString<Dimension>(jsonString)
        }
        exception.message shouldBeEqualTo "Invalid dimension format: 800x600x400. Expected format: widthxheight"
    }

    @Test
    fun `Given a dimension string with negative width, when decoding from string, then throw IllegalArgumentException`() {
        val jsonString = "\"-800x600\""

        val exception = assertThrows<IllegalArgumentException> {
            JSON_SERIALIZER.decodeFromString<Dimension>(jsonString)
        }
        exception.message shouldBeEqualTo "Width must be greater than 0, but was -800"
    }
}
