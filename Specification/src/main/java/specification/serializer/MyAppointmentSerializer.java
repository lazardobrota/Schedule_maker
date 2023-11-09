package specification.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import specification.Appointment;

import java.io.IOException;
import java.util.Map;

public class MyAppointmentSerializer extends StdSerializer<Appointment> {

    public MyAppointmentSerializer() {
        this(null);
    }

    protected MyAppointmentSerializer(Class<Appointment> t) {
        super(t);
    }

    @Override
    public void serialize(Appointment appointment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        //Room
        for (Map.Entry<String,String> entry : appointment.getRoom().getAdditionally().entrySet()) {
            jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
        }
        jsonGenerator.writeObjectField("Room name", appointment.getRoom().getRoomName());

        //Time
        for (Map.Entry<String,String> entry : appointment.getTime().getAdditionally().entrySet()) {
            jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
        }
        jsonGenerator.writeObjectField("Start", appointment.getTime().getStartDate() + " " + appointment.getTime().getStartTime());
        jsonGenerator.writeObjectField("End", appointment.getTime().getEndDate() + " " + appointment.getTime().getEndTime());
        jsonGenerator.writeObjectField("Day", appointment.getTime().getDay());

        jsonGenerator.writeEndObject();
    }
}
