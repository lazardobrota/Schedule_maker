package specification;

import lombok.Getter;

@Getter
public class Appointment {
    private Room room;
    private Time time;

    public Appointment(Room room, Time time) {
        this.room = room;
        this.time = time;
    }

    //TODO Make test to see if it works
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Appointment))
            return false;

        Appointment that = (Appointment) obj;

        //If room and time are the same
        return this.getRoom().equals(that.getRoom()) && this.getTime().equals(that.getTime());
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "room=" + room +
                ", time=" + time +
                '}';
    }
}
