import exceptions.InvalidDateException;
import implementation.DaySchedule;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DayScheduleTest {

    @Test
    public void addAppointmentTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 20), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //16.10.2023 to 20.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());
        assertEquals(5, daySchedule.getAppointments().size());

        daySchedule.getAppointments().clear();

        //Same date
        appointment.getTime().setEndDate(appointment.getTime().getStartDate());
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());
        assertEquals(1, daySchedule.getAppointments().size());

        //Same date, different day
        assertThrows(InvalidDateException.class, () -> daySchedule.addAppointment(appointment, 3));
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
