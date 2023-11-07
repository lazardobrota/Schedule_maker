package specification;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Room {
    //todo dodatni resursi i kapacitet
    private HashMap<String, String> additionally = new HashMap<>();//additional columns

    private String roomName;

    public Room(HashMap<String, String> additionally, String roomName) {
        this.additionally = additionally;
        this.roomName = roomName;
    }

    //Without additional columns
    public Room(String roomName) {
        this.roomName = roomName;
    }

    //Clone atributes constructor
    public Room(Room that) {
        this.additionally = that.getAdditionally();
        this.roomName = that.getRoomName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!(obj instanceof Room))
            return false;

        Room that = (Room) obj;

        //If they have the same address they are the same instance of the class
        if (this == that)
            return true;

        //If their name is the same
        return this.getRoomName().equals(that.getRoomName());
    }

    public Room() {
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Room{" +
                "additionally=" + additionally +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
