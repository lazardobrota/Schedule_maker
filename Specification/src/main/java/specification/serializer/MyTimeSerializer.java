package specification.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import specification.Time;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class MyTimeSerializer extends StdSerializer<Time> {

    public MyTimeSerializer() {
        this(null);
    }

    protected MyTimeSerializer(Class<Time> t) {
        super(t);
    }

    @Override
    public void serialize(Time time, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        for (Map.Entry<String,String> entry : time.getAdditionally().entrySet()) {
            jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
        }

        jsonGenerator.writeObjectField("Start date", time.getStartDate() + " : " + time.getStartTime());
        jsonGenerator.writeObjectField("End date", time.getEndDate() + " : " + time.getEndTime());
        jsonGenerator.writeObjectField("Day", time.getDay());
        jsonGenerator.writeEndObject();
    }
}
