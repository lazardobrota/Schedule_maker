package implementation;

import specification.Appointment;
import specification.Room;
import specification.Schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DaySchedule extends Schedule {

    public DaySchedule(LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
    }

    @Override
    public void initialization() {

    }

    @Override
    public boolean addRooms(Room room) {
        return this.getRooms().add(room); // returns true if element doesnt exist so its added to the list
    }

    @Override
    public boolean addAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointmentList = new ArrayList<>();
        int weeksBetween = (int) ChronoUnit.WEEKS.between(startDate, endDate);

        //Check rules

        //End date is before start date
        if (weeksBetween < 0)
            return false;

        day %= 7; //it has 7 days in the week

        if (day <= 0)
            day = 7 + day; // -1 will be Sunday

        DayOfWeek dayOfWeek = DayOfWeek.of(day);//gets day of the week
        LocalDate date = startDate;

        //How many days should be added to startDate so it starts from given day
        int addDays = dayOfWeek.getValue() - startDate.getDayOfWeek().getValue();

        if (addDays < 0)
            addDays = 7 + addDays;

        date = startDate.plusDays(addDays);

        //Makes appointments

        //TODO getTime and getRoom send pointer, it should be new instance
        for (int i = 0; i < weeksBetween; i++) {
            Appointment appoint = new Appointment(appointment.getRoom(), appointment.getTime()); //Makes new appointment
            appoint.getTime().setDate(date); //sets its date

            //Already has one of the days as appointment so it fails
            if (getAppointments().contains(appoint))
                return false;

            appointmentList.add(appoint);
            date = date.plusDays(7);//goes to next week
        }

        getAppointments().addAll(appointmentList);
        return true;
    }

    @Override
    public boolean removeAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate) {
        return false;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, LocalDate newDate) {
        return false;
    }

    @Override
    public boolean search(Appointment appointment) {
        return false;
    }
}
