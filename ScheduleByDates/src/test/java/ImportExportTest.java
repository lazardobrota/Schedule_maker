import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ImportExportTest {

    //TODO Ovi fajlovi samo kod mene postoje, nisam hteo da ih stavlja na github
    @Test
    public void importExportCSVTest() throws InvalidDateException, IOException {

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.importCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\termini.csv",
               "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
        println(scheduleByDates.getAppointments());

        scheduleByDates.exportCSV("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\output.txt",
                "E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\config.txt");
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
