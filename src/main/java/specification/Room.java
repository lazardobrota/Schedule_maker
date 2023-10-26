package specification;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class Room {
    //todo dodatni resursi i kapacitet
    private HashMap<String, String> additionally;//additional columns

    private String roomName;

    public Room(HashMap<String, String> additionally, String roomName) {
        this.additionally = additionally;
        this.roomName = roomName;
    }

    //Without additional columns
    public Room(String roomName) {
        this.roomName = roomName;
    }

    //TODO Da li treba i po HashMapi da poredi
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
}
