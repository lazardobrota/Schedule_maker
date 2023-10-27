package specification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public abstract class Schedule {

    //Begging and ending date of schedule
    private LocalDate startDate;
    private LocalDate endDate;

    private HashSet<Room> rooms = new HashSet<>();//hashSet so there is only one of every class
    private HashSet<Appointment> appointments = new HashSet<>(); //every index represents one row
    private List<LocalDate> exclusiveDays = new ArrayList<>(); // Working Sundays
    private List<LocalDate> notWorkingDays = new ArrayList<>(); // doesn't include Sunday and Saturday

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
     * Add appointment to List if there isnt already that appointment in there
     * @param appointment
     * @param day
     * @return boolean that returns TRUE if appointment has been added
     */
    public abstract boolean addAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate);

    /**
     * Removes appointment from list if it exists
     * @param appointment
     * @param day
     * @return boolean that returns TRUE if appointment has been removed
     */
    public abstract boolean removeAppointment(Appointment appointment, int day, LocalDate startDate, LocalDate endDate);

    //TODO Da li treba dva appointmenta ili drugacije provera
    /**
     * Check if old appointment exist and sets date to new date if it isnt already taken
     * @param oldAppoint needs to be removed
     * @param newDate chagnes date on oldAppoint
     * @return boolean return true if it can remove old one and add new one
     */
    public abstract boolean changeAppointment(Appointment oldAppoint, LocalDate newDate);

    /**
     * Searches for Appointment in table
     * @param appointment
     * @return boolean true if it exists
     */
    public abstract boolean search(Appointment appointment);

}
