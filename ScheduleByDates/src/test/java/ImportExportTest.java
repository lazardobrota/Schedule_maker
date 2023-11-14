import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ImportExportTest {

    //Ovi fajlovi samo kod mene postoje, nisam hteo da ih stavlja na github
    @Test
    public void importExportCSVTest() throws InvalidDateException, IOException {

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        scheduleByDates.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        scheduleByDates.importCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.csv",
               "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
        println(scheduleByDates.getAppointments());

        scheduleByDates.exportCSV("output.txt",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
    }

    private static class Car {

        private String color;
        private String type;

        public Car(String color, String type) {
            this.color = color;
            this.type = type;
        }

        public Car() {
        }

        @Override
        public String toString() {
            return "Car{" +
                    "color='" + color + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Test
    public void importExportJSONTest() throws IOException, InvalidDateException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonCarArray =
                "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
        List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
        System.out.println(listCar);


        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
        scheduleByDates.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        scheduleByDates.importJson("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.json",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");

        println(scheduleByDates.getAppointments());

        scheduleByDates.exportJson("outputjson2.txt");

    }

    @Test
    public void exportPDF() throws IOException, InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
        scheduleByDates.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        scheduleByDates.importCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.csv",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");

        scheduleByDates.exportPDF("outputPDF.pdf", "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
