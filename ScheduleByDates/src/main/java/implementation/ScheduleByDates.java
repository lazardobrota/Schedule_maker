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
            appoint.getTime().setDay(day);

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

        Appointment newAppoint = new Appointment(new Room(oldAppoint.getRoom()), new Time(oldAppoint.getTime()));
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
        List<Appointment> check;

        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Time t : times) {
            for (Appointment a: check) {
                //If true then we found element
                if (!compareTime(a.getTime(), t))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    continue;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
                tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                appointmentList.add(tmp);
            }
        }

        return appointmentList;
    }

    @Override
    public List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException{
        List<Appointment> appointmentList = new ArrayList<>();
        List<Time> times = makeTimes(time, day);
        List<Appointment> check;

        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Time t : times) {
            for (Appointment a: check) {
                //If true then we found element
                if (!(compareTime(a.getTime(), t) && compareRoom(a.getRoom(), room)))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    continue;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
                tmp.getRoom().setAdditionally(room.getAdditionally()); //Sets hashmap to empty when looking for available in range
                appointmentList.add(tmp);
            }
        }
        return appointmentList;
    }

    @Override
    public List<Appointment> search(Time time, int day, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException{
        List<Appointment> appointmentList = new ArrayList<>();
        List<Time> times = makeTimes(time, day);
        List<Appointment> check;

        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Time t : times) {
            for (Appointment a: check) {
                //If true then we found element
                if (!(compareTime(a.getTime(), t) && compareAdditional(a.getRoom().getAdditionally(), roomAdditionally)))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    continue;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
                tmp.getRoom().setAdditionally(roomAdditionally); //Sets hashmap to what it's looking for available in range
                appointmentList.add(tmp);
            }
        }
        return appointmentList;
    }

    @Override
    public List<Appointment> search(LocalDate date, boolean isAvailable) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        List<Appointment> check;
        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Appointment a: check) {
            //If true then we found element
            if (!(a.getTime().getStartDate().equals(date) && a.getTime().getEndDate().equals(date)))
                continue;

            //Looking for appointments
            if (!isAvailable) {
                appointmentList.add(a);
                continue;
            }

            //Looking for AVAILABLE appointments
            Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
            tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
            appointmentList.add(tmp);
        }
        return appointmentList;
    }

    @Override
    public List<Appointment> search(LocalDate date, Room room, boolean isAvailable) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        List<Appointment> check;
        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Appointment a: check) {
            //If true then we found element
            if (!(a.getTime().getStartDate().equals(date) && a.getTime().getEndDate().equals(date) && compareRoom(a.getRoom(), room)))
                continue;

            //Looking for appointments
            if (!isAvailable) {
                appointmentList.add(a);
                continue;
            }

            //Looking for AVAILABLE appointments
            Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
            tmp.getRoom().setAdditionally(room.getAdditionally()); //Sets hashmap to what it's looking for available in range
            appointmentList.add(tmp);
        }
        return appointmentList;
    }

    @Override
    public List<Appointment> search(LocalDate date, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException {
        List<Appointment> appointmentList = new ArrayList<>();

        List<Appointment> check;
        if (!isAvailable) //If looking for Appointments
            check = new ArrayList<>(this.getAppointments());
        else //If looking for available appointments
            check = new ArrayList<>(convertToAvailable(this.getAppointments()));

        for (Appointment a: check) {
            //If true then we found element
            if (!(a.getTime().getStartDate().equals(date) && a.getTime().getEndDate().equals(date) &&  compareAdditional(a.getRoom().getAdditionally(), roomAdditionally)))
                continue;

            //Looking for appointments
            if (!isAvailable) {
                appointmentList.add(a);
                continue;
            }

            //Looking for AVAILABLE appointments
            Appointment tmp = new Appointment(new Room(a.getRoom()), new Time(a.getTime()));
            tmp.getRoom().setAdditionally(roomAdditionally); //Sets hashmap to what it's looking for available in range
            appointmentList.add(tmp);
        }
        return appointmentList;
    }
}
