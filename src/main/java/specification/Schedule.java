package specification;

import java.util.HashSet;
import java.util.List;

public abstract class Schedule {
    private HashSet<Room> rooms;//hashSet so there is only one of every class
    private List<Appointment> appointments; //every index represents one row


    public abstract void initialization();
}
