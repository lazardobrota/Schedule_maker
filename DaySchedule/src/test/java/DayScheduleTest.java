import exceptions.InvalidDateException;
import implementation.DaySchedule;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DayScheduleTest {


    @Test
    public void addAppointmentTestTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

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
    public void searchDateTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        System.out.println("None, 18.10.2023.");
        List<Appointment> a = daySchedule.search(LocalDate.of(2023, 10, 18), false);
        println(a);
        assertEquals(0, a.size());

        //Without last and first
        System.out.println("Date that does exist: 16.10.2023.");
        time.setEndDate(LocalDate.of(2023, 10, 25));
        a = daySchedule.search(time, 1, false);
        println(a);
        assertEquals(2, a.size());
    }
    @Test
    public void searchDateDayRoomTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        daySchedule.addAppointment(appointment, 1);

        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        Time t = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 10), LocalTime.of(10, 0), LocalTime.of(12, 0));
        //10.10.2023. - 10.11.2023. raf2
        System.out.println("10.10.2023. - 10.11.2023. raf2");
        List<Appointment> a = daySchedule.search(t, 1, room, false);
        println(a);
        assertEquals(1, a.size());

        //10.10.2023. - 10.11.2023. raf2 9-12h available
        System.out.println("10.10.2023. - 10.11.2023. raf2 9-12h available");
        t.setStartTime(LocalTime.of(9, 0));
        a = daySchedule.search(t, 1, room, true);
        println(a);
        assertEquals(4, a.size());
    }

    @Test
    public void searchDateAdditionalTest() throws InvalidDateException, IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("Profesor", "Surla");
        map.put("Asistent", "Jefimija");

        Room room = new Room(map, "raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 1), LocalDate.of(2024, 1, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        daySchedule.addAppointment(appointment, 1);

        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //Search date
        System.out.println("Search date");
        List<Appointment> a = daySchedule.search(LocalDate.of(2023, 10, 16),  map, false);
        println(a);
        assertEquals(2, a.size());

        //Search available in range
        System.out.println("Search available in range");
        Time tmp = new Time(LocalDate.of(2023, 10, 5), LocalDate.of(2023, 10, 20), LocalTime.of(6, 0), LocalTime.of(17, 0));
        a = daySchedule.search(tmp, 1,  map, true);
        println(a);
        assertEquals(6, a.size());

        //Specific available
        System.out.println("Specific available");
        a = daySchedule.search(LocalDate.of(2023, 10, 16), map, true);
        println(a);
        assertEquals(4, a.size());


        map = new HashMap<>();
        map.put("Profesor", "Surla");
        map.put("Asistent", "Zdravo");
        a = daySchedule.search(LocalDate.of(2023, 10, 16), map, false);
        println(a);
        assertEquals(0, a.size());
    }

    @Test
    public void searchDateRoomTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        daySchedule.addAppointment(appointment, 1);

        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

       //16.10.2023. raf2
        System.out.println("16.10.2023. raf2");
        List<Appointment> a = daySchedule.search(LocalDate.of(2023, 10, 16), room, false);
        println(a);
        assertEquals(1, a.size());

        //18.10.2023. raf2 available
        System.out.println("18.10.2023. raf2 available");
        a = daySchedule.search(LocalDate.of(2023, 10, 18), room, true);
        println(a);
        assertEquals(1, a.size());

        System.out.println("16.10.2023. raf2 available");
        a = daySchedule.search(LocalDate.of(2023, 10, 16), room, true);
        println(a);
        assertEquals(2, a.size());
    }

    @Test
    public void searchDateDayAdditionalTest() throws InvalidDateException, IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("Profesor", "Surla");
        map.put("Asistent", "Jefimija");

        Room room = new Room(map, "raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        //table between two months
        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 1, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        daySchedule.addAppointment(appointment, 1);

        System.out.println("Add new room");
        appointment.getRoom().setRoomName("raf2");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        System.out.println("Search in range appointments");
        Time t = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 10), LocalTime.of(10, 0), LocalTime.of(12, 0));
        List<Appointment> a = daySchedule.search(t, 1, map, false);
        println(a);
        assertEquals(2, a.size());

        System.out.println("Search at 9:00h with additional available");
        t.setStartTime(LocalTime.of(9, 0));
        a = daySchedule.search(t, 1, map, true);
        println(a);
        assertEquals(8, a.size());

        //Regular search
//        System.out.println("Regular search");
//        a = daySchedule.search(LocalDate.of(2023, 10, 16), true);
//        println(a);


        map = new HashMap<>();
        map.put("Profesor", "Surla");
        map.put("Asistent", "Zdravo");
        a = daySchedule.search(t, 1, map, false);
        println(a);
        assertEquals(0, a.size());
    }

    @Test
    public void searchDateDayAvailableTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        //10.10.2023 to 30.10.2023. monday
        System.out.println("10.10.2023 to 30.10.2023. monday");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //1.10.2023. - 17.10.2023. 8-12h
        System.out.println("1.10.2023. - 17.10.2023. 8-15h");
        Time tmp = new Time(time);
        tmp.setStartTime(LocalTime.of(8, 0));
        tmp.setEndTime(LocalTime.of(15, 0));
        List<Appointment> a = daySchedule.search(tmp, 1, true);
        assertEquals(6, a.size());
        println(a);
    }

    @Test
    public void searchDateDayTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

        //10.10.2023 to 30.10.2023. monday
        System.out.println("10.10.2023 to 30.10.2023. monday");
        daySchedule.addAppointment(appointment, 1);
        println(daySchedule.getAppointments());

        //1.10.2023. - 17.10.2023.
        System.out.println("1.10.2023. - 17.10.2023.");
        Time tmp = new Time(time);
        tmp.setStartDate(LocalDate.of(2023, 10, 1));
        tmp.setEndDate(LocalDate.of(2023, 10, 17));
        List<Appointment> a = daySchedule.search(tmp, 1, false);
        assertEquals(1, a.size());
        println(a);

        //10.10.2023 to 30.10.2023. tuesday
        System.out.println("10.10.2023 to 30.10.2023. tuesday");
        daySchedule.addAppointment(appointment, 2);
        println(daySchedule.getAppointments());
        assertEquals(2, daySchedule.getAppointments().size());

        //1.10.2023. - 17.10.2023.
        System.out.println("1.10.2023. - 17.10.2023.");
        a = daySchedule.search(tmp, 2, false);
        assertEquals(1, a.size());
        println(a);
    }

    @Test
    public void removeAppointmentTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

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
        System.out.println("Between 17.10.2023. - 27.10.2023.");
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
    public void changeAppointmentTest() throws InvalidDateException, IOException {
        Room room = new Room("raf1");
        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 30), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Appointment appointment = new Appointment(room, time);

        DaySchedule daySchedule = new DaySchedule(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 11, 1));
        daySchedule.initialization("E:\\Programi\\Intellij programi\\5.semestar\\Softverske komponente\\sk-API_class_scheduler_team_lazardobrotakatarinaracic\\metaData.txt");

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

    private Appointment setDateAppoint(Appointment appointment, LocalDate startDate, LocalDate endDate) {
        appointment.getTime().setStartDate(startDate);
        appointment.getTime().setEndDate(endDate);
        return appointment;
    }
}
