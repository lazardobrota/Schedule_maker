package specification;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public abstract class Schedule {
    private HashSet<Room> rooms;//hashSet so there is only one of every class
    private HashSet<Appointment> appointments; //every index represents one row
    private List<LocalDate> exclusiveDays; // Working Sundays
    private List<LocalDate> notWorkingDays; // doesn't include Sunday and Saturday

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
     * @return boolean that returns TRUE if appointment has been added
     */
    public abstract boolean addAppointment(Appointment appointment);

    /**
     * Removes appointment from list if it exists
     * @param appointment
     * @return boolean that returns TRUE if appointment has been removed
     */
    public abstract boolean removeAppointment(Appointment appointment);

    //TODO Da li treba dva appointmenta ili drugacije provera
    /**
     * Changes appointments if old one exist
     * @param oldAppoint
     * @param newAppoint
     * @return boolean return true if it can remove old one and add new one
     */
    public abstract boolean changeAppointment(Appointment oldAppoint, Appointment newAppoint);

}
