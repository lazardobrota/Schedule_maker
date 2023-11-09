package specification.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import specification.Room;

import java.io.IOException;
import java.util.Map;

public class MyRoomSerializer extends StdSerializer<Room> {

    public MyRoomSerializer() {
        this(null);
    }

    protected MyRoomSerializer(Class<Room> t) {
        super(t);
    }

    @Override
    public void serialize(Room room, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        for (Map.Entry<String,String> entry : room.getAdditionally().entrySet()) {
            jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
        }
        jsonGenerator.writeObjectField("Room name", room.getRoomName());
        jsonGenerator.writeEndObject();
    }
}
