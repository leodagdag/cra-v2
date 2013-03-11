package utils.deserializer;

import org.bson.types.ObjectId;
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
public final class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return ObjectId.massageToObjectId(jsonParser.getText());
    }
}
