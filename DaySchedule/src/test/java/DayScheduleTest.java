import exceptions.InvalidDateException;
import implementation.DaySchedule;
import org.junit.jupiter.api.Test;
import specification.Appointment;
import specification.Room;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class DayScheduleTest {

    @Test
    public void addAppointmentTest() {
//        Room room = new Room("raf1");
//        Time time = new Time(LocalDate.of(2023, 10, 10), LocalDate.of(2023, 10, 10), LocalTime.now(), LocalTime.now().plusHours(2));
//        Appointment appointment = new Appointment(room, time);
//
//        DaySchedule daySchedule = new DaySchedule(LocalDate.now().minusYears(1), LocalDate.now());
//
//        //endDate before startDate exception
//        System.out.println(daySchedule.getAppointments());
//        InvalidDateException ex = assertThrows(InvalidDateException.class, () -> daySchedule.addAppointment(appointment, 1, LocalDate.now(), LocalDate.now().minusWeeks(2)));
//        System.out.println(ex.getMessage());
//
//        //check if everything is added correctly
//        try {
//            daySchedule.addAppointment(appointment, 1, LocalDate.now().minusWeeks(2), LocalDate.now());
//        } catch (InvalidDateException e) {
//            e.printStackTrace();
//        }
//        System.out.println(daySchedule.getAppointments());
    }
}
