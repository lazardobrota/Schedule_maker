package specification;

import lombok.Getter;

@Getter
public class Appointment implements Comparable<Appointment>{
    private Room room;
    private Time time;

    public Appointment(Room room, Time time) {
        this.room = room;
        this.time = time;
    }

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

    //Sort by room, date, hour
    //Sorter ascending(rastuce)
    //this > that return 1
    //this == that return 0
    //this < that return -1
    @Override
    public int compareTo(Appointment that) {

        int room = this.room.getRoomName().compareTo(that.getRoom().getRoomName());
        //If rooms are NOT the same
        if (room != 0) return room;

        //Same room

        //Start date
        int startDate = this.time.getStartDate().compareTo(that.getTime().getStartDate());
        if (startDate != 0) return startDate;

        //End date
        int endDate = this.time.getEndDate().compareTo(that.getTime().getEndDate());
        if (endDate != 0) return endDate;

        //Start time
        int startTime = this.time.getStartTime().compareTo(that.getTime().getStartTime());
        if (startTime != 0) return startTime;

        //End time
        return this.time.getEndTime().compareTo(that.getTime().getEndTime());
    }
}
