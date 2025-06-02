package social.bigbone

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import social.bigbone.PrecisionDateTime.InvalidPrecisionDateTime
import social.bigbone.PrecisionDateTime.ValidPrecisionDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

internal val JSON_SERIALIZER: Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

/**
 * Custom [KSerializer] to serialize and deserialize a string in the format "widthxheight"
 * into a [Dimension] object and vice versa.
 */
object DimensionSerializer : KSerializer<Dimension> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("social.bigbone.DimensionSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Dimension) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Dimension {
        val dimensionString = decoder.decodeString()

        val parts = dimensionString.split('x')
        require(parts.size == 2) {
            "Invalid dimension format: $dimensionString. Expected format: widthxheight"
        }

        try {
            val width = parts[0].toInt()
            val height = parts[1].toInt()

            require(width > 0) { "Width must be greater than 0, but was $width" }
            require(height > 0) { "Height must be greater than 0, but was $height" }

            return Dimension(width, height)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(
                "Invalid dimension values in: $dimensionString. Width and height must be integers.",
                e
            )
        }
    }
}

/**
 * Custom [KSerializer] to serialize and deserialize ISO 8601 datetime
 * or date strings into a [PrecisionDateTime] and vice versa.
 *
 * This will attempt to retrieve a [ValidPrecisionDateTime.ExactTime] first.
 * If that fails, it will try with [ValidPrecisionDateTime.StartOfDay].
 * If that _also_ fails, it will return [InvalidPrecisionDateTime.Invalid],
 * swallowing any [DateTimeParseException]s in the process.
 */
object DateTimeSerializer : KSerializer<PrecisionDateTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("social.bigbone.DateTimeSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: PrecisionDateTime) {
        return if (value is InvalidPrecisionDateTime) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value.asJsonString())
        }
    }

    override fun deserialize(decoder: Decoder): PrecisionDateTime {
        val decodedString = decoder.decodeString()

        return try {
            try {
                parseExactDateTime(decodedString)
            } catch (_: DateTimeParseException) {
                parseStartOfDay(decodedString)
            }
        } catch (dateTimeParseException: DateTimeParseException) {
            InvalidPrecisionDateTime.Invalid(dateTimeParseException)
        }
    }

    /**
     * Attempts to parse an ISO 8601 string as an exact point in time.
     * @param decodedString ISO 8601 string retrieved from JSON
     */
    private fun parseExactDateTime(decodedString: String): ValidPrecisionDateTime.ExactTime =
        ValidPrecisionDateTime.ExactTime(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(
                decodedString,
                Instant::from
            )
        )

    /**
     * Attempts to parse an ISO 8601 string into a [LocalDate] and returning an [Instant] at the start of that day in UTC.
     * @param decodedString ISO 8601 string retrieved from JSON
     */
    private fun parseStartOfDay(decodedString: String): ValidPrecisionDateTime.StartOfDay =
        ValidPrecisionDateTime.StartOfDay(LocalDate.parse(decodedString).atStartOfDay().toInstant(ZoneOffset.UTC))
}
