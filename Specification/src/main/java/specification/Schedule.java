package specification;

import exceptions.InvalidDateException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Getter
@Setter
public abstract class Schedule {

    //Begging and ending date of schedule
    private LocalDate startDate;
    private LocalDate endDate;

    private HashSet<Room> rooms;//hashSet so there is only one of every class

    //internally it will be pq, so it sorts in O(nlogn)
    //private PriorityQueue<Appointment> appointments; //every index represents one row

    private List<Appointment> appointments; //every index represents one row
    private List<LocalDate> exclusiveDays; // Working Sundays
    private List<LocalDate> notWorkingDays; // doesn't include Sunday and Saturday

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        rooms = new HashSet<>();
        appointments = new ArrayList<>();
        exclusiveDays = new ArrayList<>();
        notWorkingDays = new ArrayList<>();

        initialization();
    }

    //TODO Da li je bolje imati exception ili boolean
    //Call this function to check parametars of dates
    protected boolean isValidDate(LocalDate date) throws InvalidDateException {
        //For weekend
        //For Sunday and isn't an exclusive day
        if (date.getDayOfWeek().getValue() == 7 && !exclusiveDays.contains(date)) {
            return false;
            //throw new InvalidDateException("Sunday(7), isn't working date");
        }
        //If it's Saturday and isn't an exclusive day
        if (date.getDayOfWeek().getValue() == 6 && !exclusiveDays.contains(date)) {
            return false;
            //throw new InvalidDateException("Saturday(6), isn't working date");
        }

        //Isn't between start and end date of table
        if (date.isBefore(this.getStartDate()) || date.isAfter(this.getEndDate())) {
            return false;
            //throw new InvalidDateException("Put date inside range of table");
        }

        return true;
    }

    //TODO Fix documentation arguments
    /**
     * initializes empty table and fills list of all Exclusive days(Working Sundays) and Not working days (doesn't include Sunday and Saturday)
     */
    public abstract void initialization();

    /**
     * Adds new room to HashSet of all rooms
     * @param room
     * @return boolean that returns TRUE if room has been added
     */
    public boolean addRooms(Room room) {
        return this.getRooms().add(room); // returns true if element doesn't exist, so it's added to the list
    }

    /**
     * Add appointment to List if there isn't already that appointment in there
     * @param appointment
     * @param day
     * @return boolean that returns TRUE if appointment has been added
     */
    public abstract boolean addAppointment(Appointment appointment, int day) throws InvalidDateException;

    /**
     * Removes appointment from list if it exists
     * @param appointment
     * @param day
     * @return boolean that returns TRUE if appointment has been removed
     */
    public abstract boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException;

    //TODO Da li treba dva appointmenta ili drugacije provera
    /**
     * Check if old appointment exist and sets date to new date if it isnt already taken
     * @param oldAppoint needs to be removed
     * @return boolean return true if it can remove old one and add new one
     */
    public abstract boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException;

    //TODO Search methods need to be different
    /**
     * Searches for Appointment in table with given starting and ending dates
     * @param day search for this day
     * @param time
     * @param isAvailable does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     */
    public abstract List<Appointment> search(Time time, int day, boolean isAvailable) throws InvalidDateException;

    public abstract List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException;

    public abstract List<Appointment> search(Time time, int day, HashMap<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException;

    public abstract List<Appointment> search(LocalDate date, boolean isAvailable) throws InvalidDateException;

    public abstract List<Appointment> search(LocalDate date, Room room, boolean isAvailable) throws InvalidDateException;

    public abstract List<Appointment> search(LocalDate date, HashMap<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException;

    /* For PriorityQueue
    public List<Appointment> getAppointmentsToList() {
        List<Appointment> appointmentList = new ArrayList<>();

        while (!appointments.isEmpty()) {
            appointmentList.add(appointments.poll());
        }

        return appointmentList;
    }
     */

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

    //TODO this shouldn't be added to documentation since its private method
    /**
     * Takes arguments and makes new available Appointment
     * @param appointment real Appointment
     * @param startTime is end time of appointment before this one
     * @param endTime is start time of current appointment
     * @param startDate is end date of appointment before this one
     * @param endDate is start date of current appointment
     * @return new available Appointment between last and current appointment
     */
    protected List<Appointment> makeAvailableAppointment(Appointment appointment, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate) {
        Appointment available = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        available.getTime().setStartTime(startTime); //set start time from last appointments end time
        available.getTime().setEndTime(endTime); //set last possible time

        available.getTime().setStartDate(startDate); //set start date from last appointments end time
        available.getTime().setEndDate(endDate); //set last date on schedule

        return makeOneFromMultiDay(available);
    }

    protected List<Appointment> makeOneFromMultiDay(Appointment appointment) {
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
}