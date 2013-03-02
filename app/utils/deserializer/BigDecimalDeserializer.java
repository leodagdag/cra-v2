package utils.deserializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

/**
 * User: leodagdag
 * Date: 25/07/12
 * Time: 14:13
 */
public final class BigDecimalDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(jsonParser.getText());
    }
}
