package specification;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

//Represent set time, one instace of Time represent one row
@Getter
public class Time {
    private HashMap<String, String> additionally;//additional columns

    private LocalDate date; // only one date since class Time represent one row

    private LocalTime startTime;
    private LocalTime endTime;

    //private int day; //Calendar.DAY_OF_WEEK returns: 1 - Sunday(Nedelja), 2 - Monday, ..., 7 - Saturday(Subota)

    //Constructor will accept bool which tells if true that its 10-12h time, else it tells that its 10h + 2 so then i will change it to 10-12h
    //Constructor gets LocalDate, startTime, endTime, bool isEndTime
    //Time "from to", meaning 10-12h
    public Time(HashMap<String, String> additionally, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.additionally = additionally;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Without addicional data(HashMap)
    public Time(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Time "from + how" much, meaning 10h + 2 == 10-12h
    public Time(HashMap<String, String> additionally, LocalDate date, LocalTime startTime, long minutsToAdd) {
        this.additionally = additionally;
        this.date = date;
        this.startTime = startTime;
        this.endTime = startTime.plus(Duration.of(minutsToAdd, ChronoUnit.MINUTES)); // adds minuts to date to be "from to"
    }

    //Without addicional data(HashMap)
    public Time(LocalDate date, LocalTime startTime, long minutsToAdd) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = startTime.plus(Duration.of(minutsToAdd, ChronoUnit.MINUTES)); // adds minuts to date to be "from to"
    }
}
