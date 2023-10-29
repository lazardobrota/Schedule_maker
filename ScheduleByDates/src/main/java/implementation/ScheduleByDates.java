package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduleByDates extends Schedule {

    public ScheduleByDates(LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
    }

    @Override
    public void initialization() {

    }

    @Override
    public boolean addRooms(Room room) {
        return this.getRooms().add(room); // returns true if element doesn't exist, so it's added to the list
    }

    //Add days to startDate to be on that specific date
    private LocalDate findDateWithDay(LocalDate startDate, int day) {
        day %= 7; //it has 7 days in the week

        if (day <= 0)
            day += 7; // 0 Saturday, -1 Sunday,...

        //How many days between startDate and day we want, it only works if day is ahead then startDate
        int addDays = day - startDate.getDayOfWeek().getValue();

        //If startDate is ahead then go backwards to the day given and add 7 day to go to the next week of that day
        if (addDays < 0)
            addDays += 7;

        return startDate.plusDays(addDays);
    }

    private int weeksBetween(LocalDate startDate, LocalDate endDate) throws InvalidDateException{
        int weeksBetween = (int) ChronoUnit.WEEKS.between(startDate, endDate);

        //Check rules

        //End date is before start date
        if (weeksBetween < 0) {
            throw new InvalidDateException("Invalid startDate: " + startDate + " and endDate: "+ endDate);
        }

        return weeksBetween;
    }

    @Override
    public boolean addAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        //Check rules

        LocalDate date = findDateWithDay(startDate, day);
        int weeks = weeksBetween(date, endDate); //throws exception

        //Makes appointments

        for (int i = 0; i <= weeks; i++) {
            //Makes new appointment, calls copy Constructor
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setDate(date); //sets its date

            //Already has one of the days as appointment, so it fails
            if (getAppointments().contains(appoint))
                return false;

            appointmentList.add(appoint);
            date = date.plusDays(7);//goes to next week
        }

        //All appointments are
        getAppointments().addAll(appointmentList);
        return true;
    }

    //TODO Does it have to remove all of them or if one of them doesn't exist don't remove any?
    //TODO Add appointemnts without removing old ones
    @Override
    public boolean removeAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        LocalDate date = findDateWithDay(startDate, day);
        int weeks = weeksBetween(date, endDate); //throws exception

        for (int i = 0; i <= weeks; i++) {
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setDate(date);

            //Doesn't have one of the days as appointment, so it fails
            if (getAppointments().contains(appoint))
                return false;

            appointmentList.add(appoint);
            date = date.plusDays(7);
        }

        getAppointments().removeAll(appointmentList);
        return true;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException{

        weeksBetween(startDate, endDate); // throws exception

        //If some don't exist then it doesn't remove any
        if (!removeAppointment(oldAppoint, oldAppoint.getTime().getDate().getDayOfWeek().getValue(), oldAppoint.getTime().getDate(), oldAppoint.getTime().getDate()))
            return false;

        //If some already exist don't add any
        if (!addAppointment(oldAppoint, day, startDate, endDate)) {
            //Add back old appointments
            addAppointment(oldAppoint, oldAppoint.getTime().getDate().getDayOfWeek().getValue(), oldAppoint.getTime().getDate(), oldAppoint.getTime().getDate());
            return false;
        }
        return true;
    }

    @Override
    public List<Appointment> search(LocalDate date, Time time, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate date, Time time, Room room, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, Room room, boolean isAvailable) {
        return null;
    }


}
