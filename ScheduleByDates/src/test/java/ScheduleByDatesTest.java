import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScheduleByDatesTest {
    @Test
    public void addAppointmentTest() {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.now().minusYears(1), LocalDate.now());

        //endDate before startDate exception
        System.out.println(scheduleByDates.getAppointments());
        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> scheduleByDates.addAppointment(appointment, 1, LocalDate.now(), LocalDate.now().minusWeeks(2)));
        System.out.println(ex.getMessage());

        //check if everything is added correctly
        try {
            scheduleByDates.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
        } catch (InvalidDateException e) {
            e.printStackTrace();
        }
        System.out.println(scheduleByDates.getAppointments());
    }
}
