package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

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

    private LocalDate findDateWithDay(LocalDate startDate, int day) {
        day %= 7; //it has 7 days in the week

        if (day <= 0)
            day += 7; // 0 Saturday, -1 Sunday,...

        //How many days between startDate and day we want, it only works if day is ahead then startDate
        int addDays = day - startDate.getDayOfWeek().getValue();

        //If startDate is ahead then go backwords to the day given and add 7 day to go to the next week of that day
        if (addDays < 0)
            addDays += 7;

        return startDate.plusDays(addDays);
    }

    //TODO This method is more for other implementation from to date
    @Override
    public boolean addAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();
        int weeksBetween = (int) ChronoUnit.WEEKS.between(startDate, endDate);

        //Check rules

        //End date is before start date
        if (weeksBetween < 0) {
            throw new InvalidDateException("Invalid startDate: " + startDate + " and endDate: "+ endDate);
        }

        LocalDate date = findDateWithDay(startDate, day);

        //Makes appointments

        for (int i = 0; i < weeksBetween; i++) {
            //Makes new appointment, calls copy Constructor
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setDate(date); //sets its date

            //Already has one of the days as appointment so it fails
            if (getAppointments().contains(appoint))
                return false;

            appointmentList.add(appoint);
            date = date.plusDays(7);//goes to next week
        }

        //All appointments are
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
