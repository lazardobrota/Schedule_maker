package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.DayOfWeek;
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
            day += 7; // 0 Sunday, -1 Saturday,...

        //How many days between startDate and day we want, it only works if day is ahead then startDate
        int test = startDate.getDayOfWeek().getValue();
        DayOfWeek hello = startDate.getDayOfWeek();
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
    public boolean addAppointment(Appointment appointment, int day) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        //Check rules

        LocalDate date = findDateWithDay(appointment.getTime().getStartDate(), day);//find date of that day starting from startDate
        int weeks = weeksBetween(date, appointment.getTime().getEndDate()); //throws exception

        //Makes appointments

        for (int i = 0; i <= weeks; i++) {
            //Makes new appointment, calls copy Constructor
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setStartDate(date); //sets its date
            appoint.getTime().setEndDate(date); //sets its date

            //Already has one of the days as appointment, so it fails, or because date isn't valid in some way
            if (!isValidDate(date))
                return false;
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
    @Override
    public boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        LocalDate date = findDateWithDay(appointment.getTime().getStartDate(), day);
        int weeks = weeksBetween(date, appointment.getTime().getEndDate()); //throws exception

        for (int i = 0; i <= weeks; i++) {
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setStartDate(date);
            appoint.getTime().setEndDate(date);

            //Because date isn't valid in some way
            if (!isValidDate(date))
                return false;

            //Add to list if exist in hashset
            if (getAppointments().contains(appoint))
                appointmentList.add(appoint);

            date = date.plusDays(7);
        }

        getAppointments().removeAll(appointmentList);
        return true;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException{

        //Sets startDates to start from given day
        oldAppoint.getTime().setStartDate(findDateWithDay(oldAppoint.getTime().getStartDate(), day));
        startDate = findDateWithDay(startDate, day);

        int oldWeeksBetween = weeksBetween(oldAppoint.getTime().getStartDate(), oldAppoint.getTime().getEndDate()); //old, throws exception
        int newWeeksBetween = weeksBetween(startDate, endDate); //new, throws exception

        //Took more old dates and less new dates, so old dates can't fit in range of new dates
        if (oldWeeksBetween > newWeeksBetween)
            throw new InvalidDateException("New range needs to be bigger");

        //If newDate has bigger range, lower it to the same range as oldDate
        endDate = endDate.minusWeeks(newWeeksBetween - oldWeeksBetween);

        //If possible, remove all old appointments in that range
        if (!removeAppointment(oldAppoint, day)) {
            return false;
        }

        Appointment newAppoint = new Appointment(oldAppoint.getRoom(), oldAppoint.getTime());
        newAppoint.getTime().setStartDate(startDate);
        newAppoint.getTime().setEndDate(endDate);
        if (!addAppointment(newAppoint, day)) {
            //if it can't add new dates then
            addAppointment(oldAppoint, day);
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
