package specification;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Room {
    private Map<String, String> additionally = new HashMap<>();//additional columns

    private String roomName;

    /**
     * Make room with name and additional information
     * @param additionally additional information about the room
     * @param roomName name of room
     */
    public Room(Map<String, String> additionally, String roomName) {
        this.additionally = additionally;
        this.roomName = roomName;
    }

    /**
     * Make room with name
     * @param roomName name of room
     */
    //Without additional columns
    public Room(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Make copy of class with new address
     * @param that other Room class
     */
    //Clone atributes constructor
    public Room(Room that) {
        this.additionally = that.getAdditionally();
        this.roomName = that.getRoomName();
    }

    /**
     * Checks if two rooms are equal based on room name
     * @param obj other Room class
     * @return boolean true if they are the same
     */
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
        return this.getRoomName().equalsIgnoreCase(that.getRoomName());
    }

    /**
     * Make empty Room
     */
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
