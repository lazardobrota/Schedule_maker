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
}
