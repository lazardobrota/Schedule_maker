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
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //10.10.2023 to 30.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());
        assertEquals(1, daySchedule.getAppointments().size());

        //new Room
        System.out.println("New room");
        appointment.getRoom().setRoomName("raf2");
        assertTrue(daySchedule.addAppointment(appointment, 1));
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());

        //Same appointment
        System.out.println("Same appointment");
        assertFalse(daySchedule.addAppointment(appointment, 1));
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());

        //Dates intersect
        System.out.println("Dates intersect");
        appointment.getTime().setStartDate(LocalDate.of(2023, 10, 25));
        assertFalse(daySchedule.addAppointment(appointment, 1));
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());

        //Range in which day doesn't exist
        System.out.println("Range in which day doesn't exist");
        appointment.getTime().setStartDate(LocalDate.of(2023, 10, 10));
        appointment.getTime().setEndDate(LocalDate.of(2023, 10, 15));
        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> daySchedule.addAppointment(appointment, 1));
        System.out.println(ex.getMessage());
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());
    }

    @Test
    public void removeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //10.10.2023 to 30.10.2023. monday
        System.out.println("10.10.2023 to 30.10.2023. monday");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //Same date 10.10.2023. - 30.10.2023.
        System.out.println("Same date 10.10.2023. - 30.10.2023.");
        assertTrue(daySchedule.removeAppointment(appointment, 1));
        println(daySchedule.getAppointments());
        assertEquals(0, daySchedule.getAppointments().size());

        //Between 17.10.2023. - 27.10.2023.
        System.out.println("Between 15.10.2023. - 27.10.2023.");
        daySchedule.addAppointment(appointment, 1);
        Appointment tmp = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        tmp.getTime().setStartDate(LocalDate.of(2023, 10, 17));
        tmp.getTime().setEndDate(LocalDate.of(2023, 10, 27));
        assertTrue(daySchedule.removeAppointment(tmp, 1));
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());

        //Between addAppointment
        System.out.println("Between addAppointment");
        assertFalse(daySchedule.addAppointment(appointment, 1));
        println(daySchedule.getAppointments());

        //Right edge remove 17.10.2023. - 31.10.2023.
        System.out.println("Right edge remove 17.10.2023. - 31.10.2023.");
        daySchedule.getAppointments().clear();
        daySchedule.addAppointment(appointment, 1);
        tmp.getTime().setStartDate(LocalDate.of(2023, 10, 17));
        tmp.getTime().setEndDate(LocalDate.of(2023, 10, 31));
        assertTrue(daySchedule.removeAppointment(tmp, 1));
        println(daySchedule.getAppointments());
        assertEquals(1, daySchedule.getAppointments().size());

        //Left edge remove 10.10.2023. - 17.10.2023.
        System.out.println("Left edge remove 10.10.2023. - 17.10.2023.");
        daySchedule.getAppointments().clear();
        daySchedule.addAppointment(appointment, 1);
        tmp.getTime().setStartDate(LocalDate.of(2023, 10, 10));
        tmp.getTime().setEndDate(LocalDate.of(2023, 10, 17));
        assertTrue(daySchedule.removeAppointment(tmp, 1));
        println(daySchedule.getAppointments());
        assertEquals(1, daySchedule.getAppointments().size());

    }

    @Test
    public void changeAppointmentTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));

        //10.10.2023 to 30.10.2023. monday
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //to 1.11.2023. - 1.12.2023. but it will cut it to 24.11.2.23. because old one has 3 mondays and so should new one, it can't be bigger
        System.out.println("to 1.11.2023. - 1.12.2023. but it will cut it to 24.11.2.23. because old one has 3 mondays and so should new one, it can't be bigger");
        assertTrue(daySchedule.changeAppointment(appointment, 1, LocalDate.of(2023, 11, 1), LocalDate.of(2023, 12, 1)));
        println(daySchedule.getAppointments());

        //Smaller range of new one
        System.out.println("Smaller range of new one");
        daySchedule.getAppointments().clear();
        daySchedule.addAppointment(appointment, 1);
        InvalidDateException ex = assertThrows(InvalidDateException.class,
                () -> daySchedule.changeAppointment(appointment, 1, LocalDate.of(2023, 11, 1), LocalDate.of(2023, 11, 10)));
        System.out.println(ex.getMessage());
        println(daySchedule.getAppointments());

        /*
        //17.10.2023. - 20.10.2023. tuesday to 7.11.2023. - 30.11.2023. tuesday
        Appointment tmp = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        tmp.getTime().setStartDate(appointment.getTime().getStartDate().plusDays(1));

        assertTrue(daySchedule.changeAppointment(tmp, 2, LocalDate.of(2023, 11, 7), LocalDate.of(2023, 11, 30)));
        println(daySchedule.getAppointments());

         */
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
