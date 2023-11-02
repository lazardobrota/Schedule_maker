package specification;

import exceptions.InvalidDateException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

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
    public abstract boolean addRooms(Room room);

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

    /**
     * Searches for Appointment in table with given date
     * @param date
     * @param time
     * @param isAvailable does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     */
    public abstract List<Appointment> search(LocalDate date, Time time, boolean isAvailable);

    /**
     * Searches for Appointment in table with given starting and ending dates
     * @param startDate search from this date
     * @param endDate search until this date
     * @param day search for this day
     * @param time
     * @param isAvailable does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     */
    public abstract List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, boolean isAvailable);

    public abstract List<Appointment> search(LocalDate date, Time time, Room room, boolean isAvailable);

    public abstract List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, Room room, boolean isAvailable);

    /* For PriorityQueue
    public List<Appointment> getAppointmentsToList() {
        List<Appointment> appointmentList = new ArrayList<>();

        while (!appointments.isEmpty()) {
            appointmentList.add(appointments.poll());
        }

        return appointmentList;
    }
     */
}
