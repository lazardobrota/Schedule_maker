package specification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

//Represent set time, one instace of Time represent one row
@Getter
@Setter
public class Time {
    private HashMap<String, String> additionally;//additional columns

    //private LocalDate date; // only one date since class Time represent one row

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    //private int day; //Calendar.DAY_OF_WEEK returns: 1 - Sunday(Nedelja), 2 - Monday, ..., 7 - Saturday(Subota)

    //Constructor will accept bool which tells if true that its 10-12h time, else it tells that its 10h + 2 so then i will change it to 10-12h
    //Constructor gets LocalDate, startTime, endTime, bool isEndTime
    //Time "from to", meaning 10-12h
    public Time(HashMap<String, String> additionally, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.additionally = additionally;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Without addicional data(HashMap)
    public Time(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Time "from + how" much, meaning 10h + 2 == 10-12h
    public Time(HashMap<String, String> additionally, LocalDate startDate, LocalDate endDate, LocalTime startTime, long minutesToAdd) {
        this.additionally = additionally;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(minutesToAdd);// adds minuts to date to be "from to"
    }

    //Without addicional data(HashMap)
    public Time(LocalDate startDate, LocalDate endDate, LocalTime startTime, long minutesToAdd) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(minutesToAdd); // adds minuts to date to be "from to"
    }


    //Clone atributes constructor
    public Time(Time that) {
        this.additionally = that.getAdditionally();
        this.startDate = that.getStartDate();
        this.endDate = that.getEndDate();
        this.startTime = that.getStartTime();
        this.endTime = that.getEndTime();
    }

    //TODO Date i vreme mora da se proveri da li se preklapa sa drugim vremenom, mozda je jedna 10.10.2023. - 12.10.2023. 10-12h raf1, a drugi 11.10.2023. 10-12h raf1
    // to je i dalje preklaanje
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Time))
            return false;

        Time that = (Time) obj;

        //If they have the same address they are the same instance of the class
        if (this == that)
            return true;

        //If their START hour or minutes are different they are not the same
        if (this.getStartTime().getHour() != that.getStartTime().getHour() || this.getStartTime().getMinute() != that.getStartTime().getMinute())
            return false;

        //If their END hour or minutes are different they are not the same
        if (this.getEndTime().getHour() != that.getEndTime().getHour() || this.getEndTime().getMinute() != that.getEndTime().getMinute())
            return false;

        //If they are not the same date
        if (!this.getStartDate().equals(that.getStartDate()) || !this.getEndDate().equals(that.getEndDate()))
            return false;

        //They are on the same day and same time, so they are the same
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Time{" +
                "additionally=" + additionally +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
