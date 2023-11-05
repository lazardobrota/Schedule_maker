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
    public void addAppointmentTestTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 20), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //16.10.2023 to 20.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());
        assertEquals(5, daySchedule.getAppointments().size());

        //new Room
        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        daySchedule.getAppointments().clear();

        //Same date
        appointment.getTime().setEndDate(appointment.getTime().getStartDate());
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());
        assertEquals(1, daySchedule.getAppointments().size());

        //Same date, different day
        assertThrows(InvalidDateException.class, () -> daySchedule.addAppointment(appointment, 3));
    }

    @Test
    public void removeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 20), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //16.10.2023 to 20.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //17.10.2023 to 19.10.2023. tuesday
        appointment.getTime().setStartDate(appointment.getTime().getStartDate().plusDays(1));
        appointment.getTime().setEndDate(appointment.getTime().getEndDate().minusDays(1));
        daySchedule.removeAppointment(appointment, 2);
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());


        //Same date, different day
        appointment.getTime().setStartDate(LocalDate.of(2023, 10, 16));
        appointment.getTime().setStartDate(LocalDate.of(2023, 10, 16));
        assertThrows(InvalidDateException.class, () -> daySchedule.addAppointment(appointment, 3));
    }

    @Test
    public void changeAppointmentTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 20), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 12, 1));

        //16.10.2023 to 20.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //17.10.2023. - 20.10.2023. tuesday to 7.11.2023. - 30.11.2023. tuesday
        Appointment tmp = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        tmp.getTime().setStartDate(appointment.getTime().getStartDate().plusDays(1));

        assertTrue(daySchedule.changeAppointment(tmp, 2, LocalDate.of(2023, 11, 7), LocalDate.of(2023, 11, 30)));
        println(daySchedule.getAppointments());
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
