import implementation.DaySchedule;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;

public class DayScheduleTest {

    @Test
    public void addAppointmentTest() {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.now().minusYears(1), LocalDate.now());

        System.out.println(daySchedule.getAppointments());
        daySchedule.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
        System.out.println(daySchedule.getAppointments());
    }
}
