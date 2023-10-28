import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleByDatesTest {

    @Test
    public void addRoom() {
        Room room = new Room("raf1");
        Room room1 = new Room("raf1");
        Room room2 = new Room("raf2");
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.now().minusYears(1), LocalDate.now());

        //Does override equals and hashCode work
        System.out.println(scheduleByDates.getRooms());
        assertTrue(scheduleByDates.addRooms(room));
        assertFalse(scheduleByDates.addRooms(room));
        assertFalse(scheduleByDates.addRooms(room1));
        assertTrue(scheduleByDates.addRooms(room2));
        System.out.println(scheduleByDates.getRooms());
    }
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

    @Test
    public void changeAppointmentTest() {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.now().minusYears(1), LocalDate.now());

        //Check if it has been changes
        appointment.getTime().setDate(LocalDate.now().minusWeeks(1));
        //scheduleByDates.getAppointments().add(appointment);
        //System.out.println(scheduleByDates.getAppointments());
        //scheduleByDates.changeAppointment(appointment, LocalDate.now());
        //System.out.println(scheduleByDates.getAppointments());


        //Check with addAppointment method
        //TODO This only works on 28.10.2023., next day this code wont work since monday wont be beofre 12 days
        try {
            scheduleByDates.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
            System.out.println(scheduleByDates.getAppointments());

            appointment.getTime().setDate(LocalDate.now().minusDays(12));
            scheduleByDates.changeAppointment(appointment, LocalDate.now());
            System.out.println(scheduleByDates.getAppointments());
        } catch (InvalidDateException e) {
            e.printStackTrace();
        }
    }
}
