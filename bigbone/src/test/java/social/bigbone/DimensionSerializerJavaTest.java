package social.bigbone;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static social.bigbone.JsonSerializerKt.getJSON_SERIALIZER;

public class DimensionSerializerJavaTest {

    @Test
    void validDimensionStringToDimension() {
        @Language(value = "json", prefix = "", suffix = "")
        String jsonString = "\"800x600\"";

        Dimension decodedDimension = getJSON_SERIALIZER().decodeFromString(DimensionSerializer.INSTANCE, jsonString);

        Assertions.assertEquals(800, decodedDimension.getWidth());
        Assertions.assertEquals(600, decodedDimension.getHeight());
    }

    @Test
    void dimensionToString() {
        Dimension dimension = new Dimension(1920, 1080);

        String encodedDimension = getJSON_SERIALIZER().encodeToString(DimensionSerializer.INSTANCE, dimension);

        Assertions.assertEquals("\"1920x1080\"", encodedDimension);
    }

    @Test
    void dimensionRoundTrip() {
        Dimension dimension = new Dimension(640, 480);

        @Language(value = "json", prefix = "", suffix = "")
        String encodedDimension = getJSON_SERIALIZER().encodeToString(DimensionSerializer.INSTANCE, dimension);
        Dimension decodedDimension = getJSON_SERIALIZER().decodeFromString(DimensionSerializer.INSTANCE, encodedDimension);

        Assertions.assertEquals(dimension, decodedDimension);
    }

    @Test
    void invalidDimensionStringThrowsException() {
        @Language(value = "json", prefix = "", suffix = "") String jsonString = "\"800600\"";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            getJSON_SERIALIZER().decodeFromString(DimensionSerializer.INSTANCE, jsonString);
        });
    }
}
