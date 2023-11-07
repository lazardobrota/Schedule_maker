package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.LocalDate;
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
        LocalDate start = findDateWithDay(appointment.getTime().getStartDate(), day);

        //Adds to startDate how many days it needs to find given day, if now it's after end date that means in that range that day doesn't exist
        if (appointment.getTime().getEndDate().isBefore(start))
            throw new InvalidDateException("Day doesn't exist in that range");

        //Start date needs to be before end date
        if (appointment.getTime().getEndDate().isBefore(appointment.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");

        Appointment newAppointment = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        newAppointment.getTime().setDay(day);//sets day of new appointment

        //See if This appointment already exists or if it intersects with someone, -1 means it doesn't exist, so we want == -1
        if (compareAppontiments(newAppointment) != -1)
            return false;
        this.getAppointments().add(newAppointment);
        Collections.sort(getAppointments());
        return true;
    }

    @Override
    public boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException{
        LocalDate start = findDateWithDay(appointment.getTime().getStartDate(), day);

        //Adds to startDate how many days it needs to find given day, if now it's after end date that means in that range that day doesn't exist
        if (appointment.getTime().getEndDate().isBefore(start))
            throw new InvalidDateException("Day doesn't exist in that range");

        //Start date needs to be before end date
        if (appointment.getTime().getEndDate().isBefore(appointment.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");

        Appointment appointmentRemove = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        appointmentRemove.getTime().setDay(day);//sets day of new appointment

        //See if This appointment exists or if it intersects with someone, -1 means it doesn't exist, so we want != -1
        int index = compareAppontiments(appointmentRemove);
        if (index == -1)
            return false;

        Appointment hitAppointment = this.getAppointments().get(index);


        //hitAppointment 10.10.2023. - 30.10.2023.

        //remove 10.10.2023. - 30.10.2023., both edges
        if (hitAppointment.getTime().isDateEqual(appointmentRemove.getTime())) {
            this.getAppointments().remove(hitAppointment);
            return true;
        }

        //remove 17.10.2023. - 27.10.2023., between
        if (appointmentRemove.getTime().getStartDate().isAfter(hitAppointment.getTime().getStartDate())
                && appointmentRemove.getTime().getEndDate().isBefore(hitAppointment.getTime().getEndDate())) {
            Appointment before = new Appointment(new Room(hitAppointment.getRoom()), new Time(hitAppointment.getTime()));
            before.getTime().setEndDate(appointmentRemove.getTime().getStartDate());

            Appointment after = new Appointment(new Room(hitAppointment.getRoom()), new Time(hitAppointment.getTime()));
            after.getTime().setStartDate(appointmentRemove.getTime().getEndDate());

            this.getAppointments().remove(hitAppointment);

            //before: 10.10.2023. - 17.10.2023.
            //after:  27.10.2023. - 30.10.2023.

            //If day exist in that range
            start = findDateWithDay(before.getTime().getStartDate(), day);
            if (!before.getTime().getEndDate().isBefore(start))
                this.getAppointments().add(before); //before: 10.10.2023. - 17.10.2023.

            //If day exist in that range
            start = findDateWithDay(after.getTime().getStartDate(), day);
            if (!after.getTime().getEndDate().isBefore(start))
                this.getAppointments().add(after); //after:  27.10.2023. - 30.10.2023.
            Collections.sort(this.getAppointments());
            return true;
        }

        //remove 17.10.2023. - 30.10.2023., right edge remove
        if (hitAppointment.getTime().getStartDate().isBefore(appointmentRemove.getTime().getStartDate())) {
            //10.10.2023. - 17.10.2023.
            hitAppointment.getTime().setEndDate(appointment.getTime().getStartDate());

            //If day doesn't exist in that range
            start = findDateWithDay(hitAppointment.getTime().getStartDate(), day);
            if (hitAppointment.getTime().getEndDate().isBefore(start))
                this.getAppointments().remove(hitAppointment);
            return true;
        }

        //remove 10.10.2023. - 17.10.2023., leftEdge remove
        if (hitAppointment.getTime().getEndDate().isAfter(appointmentRemove.getTime().getEndDate())) {
            //17.10.2023. - 30.10.2023.
            hitAppointment.getTime().setStartDate(appointmentRemove.getTime().getEndDate());

            //If day doesn't exist in that range
            start = findDateWithDay(hitAppointment.getTime().getStartDate(), day);
            if (hitAppointment.getTime().getEndDate().isBefore(start))
                this.getAppointments().remove(hitAppointment);
            return true;
        }

        //It should never come to this return if everything is set up correctly
        return false;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException {
        LocalDate oldStart = findDateWithDay(oldAppoint.getTime().getStartDate(), day);
        //Adds to startDate how many days it needs to find given day, if now it's after end date that means in that range that day doesn't exist
        if (oldAppoint.getTime().getEndDate().isBefore(oldStart))
            throw new InvalidDateException("Day doesn't exist in that range");
        //Start date needs to be before end date
        if (oldAppoint.getTime().getEndDate().isBefore(oldAppoint.getTime().getStartDate()))
            throw new InvalidDateException("startDate need to be before endDate");


        LocalDate newStart = findDateWithDay(startDate, day);
        //Adds to startDate how many days it needs to find given day, if now it's after end date that means in that range that day doesn't exist
        if (endDate.isBefore(newStart))
            throw new InvalidDateException("Day doesn't exist in that range");
        //Start date needs to be before end date
        if (endDate.isBefore(startDate))
            throw new InvalidDateException("startDate need to be before endDate for new Date");

        //How many weeks between if looking from given day
        int oldWeeksBetween = weeksBetween(oldStart, oldAppoint.getTime().getEndDate()); //old, throws exception
        int newWeeksBetween = weeksBetween(newStart, endDate); //new, throws exception

        //Took more old dates and less new dates, so old dates can't fit in range of new dates
        if (oldWeeksBetween > newWeeksBetween)
            throw new InvalidDateException("New range needs to be bigger");

        //If newDate has bigger range, lower it to the same range as oldDate
        endDate = endDate.minusWeeks(newWeeksBetween - oldWeeksBetween);

        Appointment newAppoint = new Appointment(new Room(oldAppoint.getRoom()), new Time(oldAppoint.getTime()));
        newAppoint.getTime().setStartDate(startDate); //still original one
        newAppoint.getTime().setEndDate(endDate); //maby shorten to be the same size as old one

        //Couldn't add new appointment
        if (!addAppointment(newAppoint, day))
            return false;

        //Couldn't remove old appointment
        if (!removeAppointment(oldAppoint, day)) {
            //Remove new appointment that was added
            removeAppointment(newAppoint, day);
            return false;
        }

        //Added new one and removed old one
        return true;
    }

    private boolean isDateBetween(Time time, Time time2) {

        //If start dates and end dates are equal
        if (time.getStartDate().equals(time2.getStartDate()) && time.getEndDate().equals(time2.getEndDate()))
            return true;

        //If THIS StartDate is between THAT StartDate and EndDate
        if (time.getStartDate().isAfter(time2.getStartDate()) && time.getStartDate().isBefore(time2.getEndDate()))
            return true;

        //If THIS EndDate is between THAT StartDate and EndDate
        if (time.getEndDate().isAfter(time2.getStartDate()) && time.getEndDate().isBefore(time2.getEndDate()))
            return true;

        //If THAT StartDate is between THIS StartDate and EndDate
        if (time2.getStartDate().isAfter(time.getStartDate()) && time2.getStartDate().isBefore(time.getEndDate()))
            return true;

        //If THAT EndDate is between THIS StartDate and EndDate
        if (time2.getEndDate().isAfter(time.getStartDate()) && time2.getEndDate().isBefore(time.getEndDate()))
            return true;

        return false;
    }

    //TODO
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
                if (compareTime(a.getTime(), t))
                    appointmentList.add(a);

            }
        }
        return appointmentList;
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

    //-1 - aren't same
    //any num - are same
    //returns index of savedAppointment that intersects with newAppointment
    private int compareAppontiments(Appointment newAppointment) throws InvalidDateException {
        //Make single days in that range
        Appointment tmp = new Appointment(new Room(newAppointment.getRoom()), new Time(newAppointment.getTime()));
        List<Appointment> newAppointments = makeOneFromMultiDay(tmp);

        int i = -1;
        for (Appointment savedAppointment : this.getAppointments()) {
            i++;
            //They are different if their rooms aren't same
            if (!savedAppointment.getRoom().equals(newAppointment.getRoom()))
                continue;

            //They are different if their dates don't intersect
            if (!isDateBetween(savedAppointment.getTime(), newAppointment.getTime()))
                continue;

            //They are different if their days aren't same
            if (savedAppointment.getTime().getDay() != newAppointment.getTime().getDay())
                continue;

            //They are different if their times don't intersect TODO isBetweenTime treba da bude privatna metoda, mozda je vreme da se ne koristi equals
            if (!savedAppointment.getTime().isBetweenTime(newAppointment.getTime()))
                continue;

            //Same room, date, day and time
            //Make specific days to see if they are the same

            List<Appointment> savedAppointments = makeOneFromMultiDay(savedAppointment);

            //Checks if dates are the same
            for (Appointment nAppoint : newAppointments) {
                //Date isn't good
//                if (!isValidDate(nAppoint.getTime().getStartDate()))
//                    return i;

                //Already exist
                if (savedAppointments.contains(nAppoint))
                    return i;
            }
        }

        return -1;
    }
}
