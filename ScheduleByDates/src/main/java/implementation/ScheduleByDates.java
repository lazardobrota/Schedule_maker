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
import java.util.*;

public class ScheduleByDates extends Schedule {

    public ScheduleByDates(LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
    }

    @Override
    public void initialization() {
    }

    //TODO specifikacija
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

    //adds all or none
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
        Collections.sort(getAppointments());
        return true;
    }

    //it removes all in that range
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
        Collections.sort(getAppointments());
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
    public List<Appointment> search(Time time, int day, boolean isAvailable) throws InvalidDateException{
        List<Appointment> appointmentList = new ArrayList<>();
        List<Time> times = makeTimes(time, day);

        //If looking for Appointments
        if (!isAvailable) {
            for (Time t : times) {
                for (Appointment a: getAppointments()) {
                    //If true then we found element
                    if (compareTime(a.getTime(), t))
                        appointmentList.add(a);

                }
            }
            return appointmentList;
        }

        //If looking for available appointments

        //TODO
        List<Appointment> availables = convertToAvailable(this.getAppointments());
        for (Appointment a: availables) {
            //If true then we found element
            if (compareTime(a.getTime(), time))
                appointmentList.add(a);
        }

        return appointmentList;
    }

    //TODO needs to be private, its public because of testing and it needs to be in specification
    //From Appointments make list of all available appointments
    public List<Appointment> convertToAvailable(List<Appointment> appointments) {
        List<Appointment> availables = new ArrayList<>();
        LocalTime startTime = LocalTime.of(0, 0);
        LocalDate startDate = this.getStartDate();
        int i = 0;
        for (; i < appointments.size(); i++) {
            //Make new available appointment
            availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, appointments.get(i).getTime().getStartTime(), startDate, appointments.get(i).getTime().getStartDate()));

            startTime = appointments.get(i).getTime().getEndTime();//save end time for beginning of next available appointment
            startDate = appointments.get(i).getTime().getEndDate();

            //New Room so end this room and start from beginning
            if (i + 1 != appointments.size() && !appointments.get(i).getRoom().equals(appointments.get(i + 1).getRoom())) {
                //End room
                availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, LocalTime.of(23, 59), startDate, this.getEndDate()));

                //Start new Room
                startDate = this.getStartDate();
                startTime = LocalTime.of(0, 0);

            }
        }

        //End room
        i--;
        availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, LocalTime.of(23, 59), startDate, this.getEndDate()));

        return availables;
    }

    //TODO this should be added to documentation since its private method
    /**
     * Takes arguments and makes new available Appointment
     * @param appointment real Appointment
     * @param startTime is end time of appointment before this one
     * @param endTime is start time of current appointment
     * @param startDate is end date of appointment before this one
     * @param endDate is start date of current appointment
     * @return new available Appointment between last and current appointment
     */
    private List<Appointment> makeAvailableAppointment(Appointment appointment, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate) {
        Appointment available = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        available.getTime().setStartTime(startTime); //set start time from last appointments end time
        available.getTime().setEndTime(endTime); //set last possible time

        available.getTime().setStartDate(startDate); //set start date from last appointments end time
        available.getTime().setEndDate(endDate); //set last date on schedule

        return makeOneFromMultiDay(available);
    }

    private List<Appointment> makeOneFromMultiDay(Appointment appointment) {
        List<Appointment> appointments = new ArrayList<>();


        boolean flag = false;
        while (!appointment.getTime().getStartDate().equals(appointment.getTime().getEndDate())) {
            Appointment a = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));

            a.getTime().setStartTime(LocalTime.of(0, 0));
            a.getTime().setEndTime(LocalTime.of(23, 59));
            a.getTime().setEndDate(a.getTime().getStartDate());
            //First time start from apponitments start time
            if (!flag) {
                a.getTime().setStartTime(appointment.getTime().getStartTime());
                flag = true;
            }
            appointments.add(a);

            //Go to next day
            appointment.getTime().setStartDate(appointment.getTime().getStartDate().plusDays(1));
        }

        //If flag is true it means there were days between dates so last day start from 00:00
        if (flag)
            appointment.getTime().setStartTime(LocalTime.of(0, 0));
        appointments.add(appointment);//And last day
        return appointments;
    }

    @Override
    public List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException{
        return null;
    }

    @Override
    public List<Appointment> search(Time time, int day, HashMap<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException{
        return null;
    }

    //Takes range of time and makes more one day times
    private List<Time> makeTimes(Time time, int day) throws InvalidDateException{
        List<Time> times = new ArrayList<>();

        LocalDate date = findDateWithDay(time.getStartDate(), day);
        int weeks = weeksBetween(date, time.getEndDate()); //throws exception

        for (int i = 0; i <= weeks; i++) {
            Time t = new Time(time);
            t.setStartDate(date);
            t.setEndDate(date);

            times.add(t);

            date = date.plusDays(7);
        }

        return times;
    }

    private boolean compareTime(Time time, Time time2) {
        //Different with overridden equals method
        if (!time.equals(time2))
            return false;

        //Doesn't look for anything in hashMap
        if (time2.getAdditionally() == null)
            return true;

        //Looking for additional that doesn't exist at all
        if (time.getAdditionally() == null)
            return false;

        for (Map.Entry<String, String> set : time2.getAdditionally().entrySet()) {
            //If our element doesn't have that key, or they are not the same, return false
            if (!time.getAdditionally().containsKey(set.getKey()) || !time.getAdditionally().get(set.getKey()).equals(set.getValue()))
                return false;
        }

        return true;
    }
}
