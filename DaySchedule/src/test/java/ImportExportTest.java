import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.InvalidDateException;
import implementation.DaySchedule;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ImportExportTest {

    @Test
    public void importExportCSVTest() throws InvalidDateException, IOException {

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        daySchedule.importCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.csv",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
        println(daySchedule.getAppointments());

        daySchedule.exportCSV("output.txt",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
    }

    @Test
    public void importExportJSONTest() throws IOException, InvalidDateException {

        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));

        daySchedule.importJson("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.json",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");

        println(daySchedule.getAppointments());

        daySchedule.exportJson("outputjson3.txt");

    }

    @Test
    public void exportPDF() throws IOException, InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));

        daySchedule.importCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.csv",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");

        daySchedule.exportPDF("outputPDF.pdf", "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
