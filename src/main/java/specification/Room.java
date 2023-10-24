package specification;

import java.util.HashMap;

public class Room {
    private HashMap<String, String> additionally;//additional columns

    private String roomName;

    public Room(HashMap<String, String> additionally, String roomName) {
        this.additionally = additionally;
        this.roomName = roomName;
    }
}
