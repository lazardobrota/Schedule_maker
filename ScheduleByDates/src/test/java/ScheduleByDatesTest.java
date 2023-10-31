import exceptions.InvalidDateException;
import implementation.ScheduleByDates;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;

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
    public void addAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now().minusHours(3), LocalTime.now());
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        //endDate before startDate exception
        System.out.println(scheduleByDates.getAppointments());
        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> scheduleByDates.addAppointment(appointment, 1, LocalDate.now(), LocalDate.now().minusWeeks(2)));
        System.out.println(ex.getMessage());

        //check if everything is added correctly
        scheduleByDates.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
        System.out.println(scheduleByDates.getAppointments());

        scheduleByDates.getAppointments().clear();//clear list

        //klasa ekvivalencije
        assertAll(
                //End date before start date
                () -> assertThrows(InvalidDateException.class, () -> scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 30), LocalDate.of(2023, 10, 10))),

                //Dates before, that table doesn't support
                () -> assertFalse(scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2022, 10, 10), LocalDate.of(2022, 10, 30))),
                //Dates after, that table doesn't support
                () -> assertFalse(scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 30))),
                () -> assertTrue(scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 25)))
        );

        scheduleByDates.getAppointments().clear();//clear list

        //analiza granicnih vrednosti
        assertAll(
                () -> assertFalse(scheduleByDates.addAppointment(appointment, 2, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 2))),
                () -> assertTrue(scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 1))),
                () -> assertTrue(scheduleByDates.addAppointment(appointment, 4, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 10, 30))),
                () -> assertFalse(scheduleByDates.addAppointment(appointment, 3, LocalDate.of(2023, 5, 31), LocalDate.of(2023, 10, 2)))
        );

        scheduleByDates.getAppointments().clear();//clear list

        //Nagadjanje gresaka
        //iako je van datuma ponedelja se ne nalazi van, poslednji put je 2023.12.31
        assertTrue(scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 5)));
    }

    @Test
    public void removeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));

        scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30));
        System.out.println(scheduleByDates.getAppointments());
        //klasa ekvivalencije
        assertAll(
                //End date before start date
                () -> assertThrows(InvalidDateException.class, () -> scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2023, 10, 30), LocalDate.of(2023, 10, 10))),

                //Dates before, that table doesn't support
                () -> assertFalse(scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2022, 10, 10), LocalDate.of(2022, 10, 30))),
                //Dates after, that table doesn't support
                () -> assertFalse(scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 30))),
                () -> assertTrue(scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 25)))
        );
        System.out.println(scheduleByDates.getAppointments() + "\n\n");
        scheduleByDates.getAppointments().clear();//clear list

        scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30));
        System.out.println(scheduleByDates.getAppointments());
        //analiza granicnih vrednosti
        assertAll(
                () -> assertFalse(scheduleByDates.removeAppointment(appointment, 2, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 2))),
                () -> assertTrue(scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 1))),
                () -> assertTrue(scheduleByDates.removeAppointment(appointment, 4, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 10, 30))),
                () -> assertFalse(scheduleByDates.removeAppointment(appointment, 3, LocalDate.of(2023, 5, 31), LocalDate.of(2023, 10, 2)))
        );
        System.out.println(scheduleByDates.getAppointments());

        //Nagadjanje gresaka
        //iako je van datuma ponedelja se ne nalazi van, poslednji put je 2023.12.31
        assertTrue(scheduleByDates.removeAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 1, 5)));
    }

    @Test
    public void changeAppointmentTest() throws InvalidDateException {
        Room room = new Room("raf1");
        Time time = new Time(LocalTime.now(), LocalTime.now().plusHours(2));
        Appointment appointment = new Appointment(room, time);

        ScheduleByDates scheduleByDates = new ScheduleByDates(LocalDate.now().minusYears(1), LocalDate.now());

        //Check if it has been changes
        scheduleByDates.addAppointment(appointment, 1, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30));
        System.out.println(scheduleByDates.getAppointments());
        scheduleByDates.changeAppointment(appointment, 5, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30));
        System.out.println(scheduleByDates.getAppointments());


        //Check with addAppointment method
        //TODO This only works on 28.10.2023., next day this code wont work since monday wont be before 12 days
//        try {
//            scheduleByDates.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
//            System.out.println(scheduleByDates.getAppointments());
//
//            appointment.getTime().setDate(LocalDate.now().minusDays(12));
//            scheduleByDates.changeAppointment(appointment, 5, LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 17));
//            System.out.println(scheduleByDates.getAppointments());
//        } catch (InvalidDateException e) {
//            e.printStackTrace();
//        }
    }
}
