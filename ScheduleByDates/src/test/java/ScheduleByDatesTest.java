import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    @Disabled
    //Needs to be fixed
    public void searchDate() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(appointment, 1);

        //Without first one
        time.setStartDate(LocalDate.of(2023, 10, 18));
        List<Appointment> a = scheduleByDates.search(time, 1, false);
        println(a);
        assertEquals(2, a.size());

        //Without last and first
        time.setEndDate(LocalDate.of(2023, 10, 25));
        a = scheduleByDates.search(time, 1, false);
        println(a);
        assertEquals(1, a.size());

        //None selected
        setDateAppoint(appointment, LocalDate.of(2023, 9, 1), LocalDate.of(2023, 9, 30));
        a = scheduleByDates.search(time, 1, false);
        println(a);
        assertEquals(0, a.size());
    }

    @Test
    public void searchDateAvailable() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(appointment, 1);
        println(scheduleByDates.getAppointments());

        List<Appointment> a = scheduleByDates.search(LocalDate.of(2023, 10, 17),  true);
        println(a);
    }

    @Test
    public void searchDateTime() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(appointment, 1);

        //Set different time that doesn't exist in , 10-12h and 12:01-14h
        time.setStartTime(LocalTime.of(12, 1));
        time.setEndTime(LocalTime.of(14, 0));
        List<Appointment> a = scheduleByDates.search(time, 1, false);
        println(a);
        assertEquals(0, a.size());

        //Set time that intersect with existing appointments in list, 10-12h and 11-14h
        time.setStartTime(LocalTime.of(11, 0));
        a = scheduleByDates.search(time, 1, false);
        println(a);
        assertEquals(3, a.size());
    }

    @Test
    public void searchDateTimeAvailable() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(appointment, 1);
        appointment.getRoom().setRoomName("raf2");
        scheduleByDates.addAppointment(appointment, 1);

        appointment.getTime().setStartDate(LocalDate.of(2023, 10, 1));
        appointment.getTime().setEndDate(LocalDate.of(2023, 10, 30));
        appointment.getTime().setStartTime(LocalTime.of(8, 0));
        appointment.getTime().setEndTime(LocalTime.of(9, 0));
        List<Appointment> a = scheduleByDates.search(appointment.getTime(), 1, true);
        println(a);
        assertEquals(10, a.size());

        scheduleByDates.addAppointment(appointment, 1);
        println(scheduleByDates.getAppointments());

        //Between two appointments on same date
        appointment.getTime().setEndTime(LocalTime.of(11, 0));
        a = scheduleByDates.search(appointment.getTime(), 1, true);
        println(a);
        assertEquals(10, a.size());
    }
    @Test
    public void timeEqualsTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0, 40), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        scheduleByDates.addAppointment(appointment, 1);


        assertFalse(scheduleByDates.addAppointment(appointment, 1));
    }

    @Test
    public void addAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now().minusHours(3), LocalTime.now());
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        //endDate before startDate exception
        System.out.println(scheduleByDates.getAppointments());
        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.now(), LocalDate.now().minusWeeks(2)), 1));
        System.out.println(ex.getMessage());

        //check if everything is added correctly
        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30)), 1);
        System.out.println(scheduleByDates.getAppointments());

        scheduleByDates.getAppointments().clear();//clear list

        //klasa ekvivalencije
        assertAll(
                //End date before start date
                () -> assertThrows(InvalidDateException.class, () -> scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 30), LocalDate.of(2023, 10, 10)), 1)),

                //Dates before, that table doesn't support
                () -> assertFalse(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2022, 10, 10), LocalDate.of(2022, 10, 30)), 1)),
                //Dates after, that table doesn't support
                () -> assertFalse(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 30)), 1)),
                () -> assertTrue(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 25)), 1))
        );

        scheduleByDates.getAppointments().clear();//clear list

        //analiza granicnih vrednosti
        assertAll(
                () -> assertFalse(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 2)), 2)),
                () -> assertTrue(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 1)), 1)),
                () -> assertTrue(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 10, 30)), 4)),
                () -> assertFalse(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 5, 31), LocalDate.of(2023, 10, 2)), 3))
        );

        scheduleByDates.getAppointments().clear();//clear list

        //Nagadjanje gresaka
        //iako je van datuma ponedelja se ne nalazi van, poslednji put je 2023.12.31
        assertTrue(scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 5)), 1));
    }

    @Test
    public void removeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30)), 1);
        System.out.println(scheduleByDates.getAppointments());
        //klasa ekvivalencije

        //End date before start date
        assertThrows(InvalidDateException.class, () -> scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 30), LocalDate.of(2023, 10, 10)), 1));

        //Dates before, that table doesn't support
        assertFalse(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2022, 10, 10), LocalDate.of(2022, 10, 30)), 1));
        //Dates after, that table doesn't support
        assertFalse(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 30)), 1));
        assertTrue(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 25)), 1));
        System.out.println(scheduleByDates.getAppointments() + "\n\n");
        scheduleByDates.getAppointments().clear();//clear list

        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30)), 1);
        System.out.println(scheduleByDates.getAppointments());

        //analiza granicnih vrednosti
        assertFalse(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 2)), 2));
        assertTrue(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 1)), 1));
        assertTrue(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 10, 30)), 4));
        assertFalse(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 5, 31), LocalDate.of(2023, 10, 2)), 3));
        System.out.println(scheduleByDates.getAppointments());

        //Nagadjanje gresaka
        //iako je van datuma ponedelja se ne nalazi van, poslednji put je 2023.12.31
        assertTrue(scheduleByDates.removeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 5)), 1));
    }

    @Test
    public void convertToAvailableTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));


        scheduleByDates.addAppointment(appointment, 1);
        println(scheduleByDates.getAppointments());
        List<Appointment> a = scheduleByDates.convertToAvailable(scheduleByDates.getAppointments());
        println(a);
        assertEquals(26, a.size());

        appointment.getRoom().setRoomName("raf2");
        scheduleByDates.addAppointment(appointment, 1);
        println(scheduleByDates.getAppointments());
        a = scheduleByDates.convertToAvailable(scheduleByDates.getAppointments());
        println(a);
        assertEquals(52, a.size());

        System.out.println("Something different");
        scheduleByDates.getAppointments().clear();
        appointment.getTime().setStartTime(LocalTime.of(0, 0));
        appointment.getTime().setEndTime(LocalTime.of(23, 59));
        scheduleByDates.addAppointment(appointment, 1);
        scheduleByDates.addAppointment(appointment, 2);
        println(scheduleByDates.getAppointments());
        a = scheduleByDates.convertToAvailable(scheduleByDates.getAppointments());
        println(a);
    }

    @Test
    @Disabled
    public void sortTest() throws InvalidDateException{
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 12, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));


        scheduleByDates.addAppointment(appointment, 1);
        println(scheduleByDates.getAppointments());

        System.out.println("\n\n\n");
        appointment.getRoom().setRoomName("raf2");
        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 9, 30)), 5);
        println(scheduleByDates.getAppointments());

        System.out.println("\nClear\n\n\n");
        scheduleByDates.getAppointments().clear();
        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 12, 30)), 5);

        println(scheduleByDates.getAppointments());
        System.out.println("\n\n\n");
        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 9, 30)), 5);
        println(scheduleByDates.getAppointments());
    }

    @Test
    public void changeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));

        //Check if it has been changes
        scheduleByDates.addAppointment(setDateAppoint(appointment, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30)), 1);
        System.out.println("Original:\n" + scheduleByDates.getAppointments());

        //Simple change with same amount of range
        assertTrue(scheduleByDates.changeAppointment(appointment, 1, LocalDate.of(2023, 11, 10), LocalDate.of(2023, 11, 30)));
        System.out.println("Same range: \n" + scheduleByDates.getAppointments());


        //New Appointment has bigger range
        assertTrue(scheduleByDates.changeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 11, 10), LocalDate.of(2023, 11, 30)),
                1, LocalDate.of(2023, 9, 10), LocalDate.of(2023, 10, 30)));
        System.out.println("newAppoint bigger range: \n" + scheduleByDates.getAppointments());

        //New Appointment has shorter range
        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> scheduleByDates.changeAppointment(setDateAppoint(appointment, LocalDate.of(2023, 9, 10), LocalDate.of(2023, 9, 30)),
                1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 15)));

        System.out.println(ex.getMessage());
        System.out.println(scheduleByDates.getAppointments());

        //Check with addAppointment method
        //TODO This only works on 28.10.2023., next day this code wont work since monday wont be before 12 days
    }

    private Appointment setDateAppoint(Appointment appointment, LocalDate startDate, LocalDate endDate) {
        appointment.getTime().setStartDate(startDate);
        appointment.getTime().setEndDate(endDate);
        return appointment;
    }

    private void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }
}
