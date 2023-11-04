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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DaySchedule extends Schedule {

    public DaySchedule(LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
    }

    @Override
    public void initialization() {

    }

    @Override
    public boolean addAppointment(Appointment appointment, int day) throws InvalidDateException {
        if (appointment.getTime().getStartDate().getDayOfWeek().getValue() != day)
            throw new InvalidDateException("Day needs to be the same as startDate");

        if (appointment.getTime().getEndDate().isBefore(appointment.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");

        List<Appointment> appointments = makeOneFromMultiDay(new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime())));

        for (Appointment a : appointments) {
            //Date isn't good
            if (!isValidDate(a.getTime().getStartDate()))
                return false;

            //Already exist
            if (this.getAppointments().contains(a))
                return false;
        }

        this.getAppointments().addAll(appointments);
        Collections.sort(getAppointments());
        return true;
    }

    @Override
    public boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException{
        if (appointment.getTime().getStartDate().getDayOfWeek().getValue() != day)
            throw new InvalidDateException("Day needs to be the same as startDate");

        if (appointment.getTime().getEndDate().isBefore(appointment.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");

        List<Appointment> appointments = makeOneFromMultiDay(new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime())));
        List<Appointment> toRemove = new ArrayList<>();
        for (Appointment a : appointments) {
            //Date isn't good
            if (!isValidDate(a.getTime().getStartDate()))
                return false;

            //It exists in the list
            if (this.getAppointments().contains(a))
                toRemove.add(a);
        }

        this.getAppointments().removeAll(toRemove);
        Collections.sort(this.getAppointments());
        return true;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException {
        if (oldAppoint.getTime().getStartDate().getDayOfWeek().getValue() != day)
            throw new InvalidDateException("Day needs to be the same as startDate for old Date");
        if (oldAppoint.getTime().getEndDate().isBefore(oldAppoint.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");

        if (startDate.getDayOfWeek().getValue() != day)
            throw new InvalidDateException("Day needs to be the same as startDate");
        if (endDate.isBefore(startDate))
            throw new InvalidDateException("startDate need to be before endDate for new Date");

        long oldDaysBetween = ChronoUnit.DAYS.between(oldAppoint.getTime().getStartDate(), oldAppoint.getTime().getEndDate()); //old, throws exception
        long newDaysBetween = ChronoUnit.DAYS.between(startDate, endDate); //new, throws exception

        //Took more old dates and less new dates, so old dates can't fit in range of new dates
        if (oldDaysBetween > newDaysBetween)
            throw new InvalidDateException("New range needs to be bigger");

        //If newDate has bigger range, lower it to the same range as oldDate
        endDate = endDate.minusDays(newDaysBetween - oldDaysBetween);

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
        return null;
    }

    @Override
    public List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException{
        return null;
    }

    @Override
    public List<Appointment> search(Time time, int day, HashMap<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException{
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate date, boolean isAvailable) throws InvalidDateException {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate date, Room room, boolean isAvailable) throws InvalidDateException {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate date, HashMap<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException {
        return null;
    }


}
