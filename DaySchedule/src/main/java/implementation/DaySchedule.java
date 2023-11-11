package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        //Start time needs to be before end time
        if (appointment.getTime().getEndTime().isBefore(appointment.getTime().getStartTime()))
            throw new InvalidDateException("startTime need to be before endTime");

        Appointment newAppointment = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        newAppointment.getTime().setDay(day);//sets day of new appointment

        //See if This appointment already exists or if it intersects with someone, -1 means it doesn't exist, so we want == -1
//        if (compareAppontiments(newAppointment) != -1)
//            return false;
        //See if This appointment already exists or if it intersects with someone, 0 means it doesn't exist, so we want == 0
        if (compareAppontiments(newAppointment).size() > 0)
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
        //Start time needs to be before end time
        if (appointment.getTime().getEndTime().isBefore(appointment.getTime().getStartTime()))
            throw new InvalidDateException("startTime need to be before endTime");

        Appointment appointmentRemove = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        appointmentRemove.getTime().setDay(day);//sets day of new appointment

        //See if This appointment exists or if it intersects with someone, -1 means it doesn't exist, so we want != -1
        List<Appointment> hitAppointments = compareAppontiments(appointmentRemove);
//        int index = compareAppontiments(appointmentRemove);
//        if (index == -1)
//            return false;

        //See if This appointment exists or if it intersects with someone, 0 means it doesn't exist, so we want > 0
        if (hitAppointments.size() == 0)
            return false;

        for (Appointment hitAppointment : hitAppointments) {
            //hitAppointment 10.10.2023. - 30.10.2023.

            //remove 10.10.2023. - 30.10.2023., both edges
            if (hitAppointment.getTime().isDateEqual(appointmentRemove.getTime())) {
                this.getAppointments().remove(hitAppointment);
                continue;
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
                continue;
            }

            //remove 17.10.2023. - 30.10.2023., right edge remove
            if (hitAppointment.getTime().getStartDate().isBefore(appointmentRemove.getTime().getStartDate())) {
                //10.10.2023. - 17.10.2023.
                hitAppointment.getTime().setEndDate(appointment.getTime().getStartDate());

                //If day doesn't exist in that range
                start = findDateWithDay(hitAppointment.getTime().getStartDate(), day);
                if (hitAppointment.getTime().getEndDate().isBefore(start))
                    this.getAppointments().remove(hitAppointment);
                continue;
            }

            //remove 10.10.2023. - 17.10.2023., leftEdge remove
            if (hitAppointment.getTime().getEndDate().isAfter(appointmentRemove.getTime().getEndDate())) {
                //17.10.2023. - 30.10.2023.
                hitAppointment.getTime().setStartDate(appointmentRemove.getTime().getEndDate());

                //If day doesn't exist in that range
                start = findDateWithDay(hitAppointment.getTime().getStartDate(), day);
                if (hitAppointment.getTime().getEndDate().isBefore(start))
                    this.getAppointments().remove(hitAppointment);
                continue;
            }

            //It should never come to this return if everything is set up correctly
            return false;
        }

        return true;
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
        //Start time needs to be before end time
        if (oldAppoint.getTime().getEndTime().isBefore(oldAppoint.getTime().getStartTime()))
            throw new InvalidDateException("startTime need to be before endTime");


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

    @Override
    public List<Appointment> search(Time time, int day, boolean isAvailable) throws InvalidDateException{
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range
        List<Time> times = makeTimes(time, day);

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Time t : times) {
            for (Appointment a: this.getAppointments()) {
                List<Appointment> oneDays;
                if (!isAvailable) //If looking for Appointments
                    oneDays = convertToDays(a); //range to multi days with specific day
                else //If looking for available appointments
                    oneDays = days;

                for (Appointment oneDay : oneDays) {
                    //If true then we found element
                    if (!compareTime(oneDay.getTime(), t))
                        continue;

                    //Looking for appointments
                    if (!isAvailable) {
                        appointmentList.add(a);
                        break;
                    }

                    //Looking for AVAILABLE appointments
                    Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                    tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                    appointmentList.add(tmp);
                }
            }
        }
        return new ArrayList<>(appointmentList);
    }

    @Override
    public List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException{
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range
        List<Time> times = makeTimes(time, day);

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Time t : times) {
            for (Appointment a: this.getAppointments()) {
                List<Appointment> oneDays;
                if (!isAvailable) //If looking for Appointments
                    oneDays = convertToDays(a); //range to multi days with specific day
                else //If looking for available appointments
                    oneDays = days;

                for (Appointment oneDay : oneDays) {
                    //If true then we found element
                    if (!(compareTime(oneDay.getTime(), t) && compareRoom(oneDay.getRoom(), room)))
                        continue;

                    //Looking for appointments
                    if (!isAvailable) {
                        appointmentList.add(a);
                        break;
                    }

                    //Looking for AVAILABLE appointments
                    Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                    tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                    appointmentList.add(tmp);
                }
            }
        }
        return new ArrayList<>(appointmentList);
    }

    @Override
    public List<Appointment> search(Time time, int day, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException{
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range
        List<Time> times = makeTimes(time, day);

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Time t : times) {
            for (Appointment a: this.getAppointments()) {
                List<Appointment> oneDays;
                if (!isAvailable) //If looking for Appointments
                    oneDays = convertToDays(a); //range to multi days with specific day
                else //If looking for available appointments
                    oneDays = days;

                for (Appointment oneDay : oneDays) {
                    //If true then we found element
                    if (!(compareTime(oneDay.getTime(), t) && compareAdditional(oneDay.getRoom().getAdditionally(), roomAdditionally)))
                        continue;

                    //Looking for appointments
                    if (!isAvailable) {
                        appointmentList.add(a);
                        break;
                    }

                    //Looking for AVAILABLE appointments
                    Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                    tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                    appointmentList.add(tmp);
                }
            }
        }
        return new ArrayList<>(appointmentList);
    }

    @Override
    public List<Appointment> search(LocalDate date, boolean isAvailable) throws InvalidDateException {
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Appointment a: this.getAppointments()) {
            List<Appointment> oneDays;
            if (!isAvailable) //If looking for Appointments
                oneDays = convertToDays(a); //range to multi days with specific day
            else //If looking for available appointments
                oneDays = days;

            for (Appointment oneDay : oneDays) {
                //If true then we found element
                if (!(oneDay.getTime().getStartDate().equals(date) && oneDay.getTime().getEndDate().equals(date)))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    break;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                appointmentList.add(tmp);
            }
        }
        return new ArrayList<>(appointmentList);
    }

    @Override
    public List<Appointment> search(LocalDate date, Room room, boolean isAvailable) throws InvalidDateException {
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Appointment a: this.getAppointments()) {
            List<Appointment> oneDays;
            if (!isAvailable) //If looking for Appointments
                oneDays = convertToDays(a); //range to multi days with specific day
            else //If looking for available appointments
                oneDays = days;

            for (Appointment oneDay : oneDays) {
                //If true then we found element
                if (!(oneDay.getTime().getStartDate().equals(date) && oneDay.getTime().getEndDate().equals(date) && compareRoom(oneDay.getRoom(), room)))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    break;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                appointmentList.add(tmp);
            }
        }
        return new ArrayList<>(appointmentList);
    }

    @Override
    public List<Appointment> search(LocalDate date, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException {
        Set<Appointment> appointmentList = new HashSet<>(); //hashset so duplicates can't spawn if adding range

        //If looking for available turn everything in single day and then turn that in available
        List<Appointment> days = new ArrayList<>();
        if (isAvailable) {
            for (Appointment a : this.getAppointments()) {
                days.addAll(convertToDays(a));
            }
            days = new ArrayList<>(new ArrayList<>(convertToAvailable(days)));
        }

        for (Appointment a: this.getAppointments()) {
            List<Appointment> oneDays;
            if (!isAvailable) //If looking for Appointments
                oneDays = convertToDays(a); //range to multi days with specific day
            else //If looking for available appointments
                oneDays = days;

            for (Appointment oneDay : oneDays) {
                //If true then we found element
                if (!(oneDay.getTime().getStartDate().equals(date) && oneDay.getTime().getEndDate().equals(date) &&  compareAdditional(oneDay.getRoom().getAdditionally(), roomAdditionally)))
                    continue;

                //Looking for appointments
                if (!isAvailable) {
                    appointmentList.add(a);
                    break;
                }

                //Looking for AVAILABLE appointments
                Appointment tmp = new Appointment(new Room(oneDay.getRoom()), new Time(oneDay.getTime()));
                tmp.getRoom().setAdditionally(new HashMap<>()); //Sets hashmap to empty when looking for available in range
                appointmentList.add(tmp);
            }
        }
        return new ArrayList<>(appointmentList);
    }

    private List<Appointment> convertToDays(Appointment appointment) throws InvalidDateException {
        List<Appointment> appointments = new ArrayList<>();

        LocalDate date = findDateWithDay(appointment.getTime().getStartDate(), appointment.getTime().getDay());
        int weeks = weeksBetween(date, appointment.getTime().getEndDate()); //throws exception

        for (int i = 0; i <= weeks; i++) {
            Appointment appoint = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
            appoint.getTime().setStartDate(date);
            appoint.getTime().setEndDate(date);

            appointments.add(appoint);

            date = date.plusDays(7);
        }

        Collections.sort(appointments);
        return appointments;
    }

    //-1 - aren't same, 0
    //any num - are same
    //returns index of savedAppointment that intersects with newAppointment
    private List<Appointment> compareAppontiments(Appointment newAppointment) throws InvalidDateException {

        List<Appointment> toReturn = new ArrayList<>();
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

            //They are different if their times don't intersect
            if (!savedAppointment.getTime().isBetweenTime(newAppointment.getTime()))
                continue;

            //Same room, date, day and time
            //Make specific days to see if they are the same

            toReturn.add(savedAppointment);
        }

        return toReturn; //-1
    }
}
