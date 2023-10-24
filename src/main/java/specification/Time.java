package specification;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Time {
    private HashMap<String, String> additionally;//additional columns

    private LocalDateTime from;
    private LocalDateTime to;

    private int day; //Calendar.DAY_OF_WEEK returns: 1 - Sunday(Nedelja), 2 - Monday, ..., 7 - Saturday(Subota)

    //TODO Constructor will accept bool which tells if true that its 10-12h time, else it tells that its 10h + 2 so then i will change it to 10-12h
    public Time(HashMap<String, String> additionally, LocalDateTime from, LocalDateTime to, int day) {
        this.additionally = additionally;
        this.from = from;
        this.to = to;
        this.day = day;
    }
}
