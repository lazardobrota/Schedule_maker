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
    private HashMap<String, String> additionally = new HashMap<>();//additional columns

    //private LocalDate date; // only one date since class Time represent one row

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private int day;

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

    //Just dates for range when doing search
//    public Time(LocalDate startDate, LocalDate endDate) {
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }

    //Clone atributes constructor
    public Time(Time that) {
        this.additionally = that.getAdditionally();
        this.startDate = that.getStartDate();
        this.endDate = that.getEndDate();
        this.startTime = that.getStartTime();
        this.endTime = that.getEndTime();
        this.day = that.getDay();
    }

    //TODO Date i vreme mora da se proveri da li se preklapa sa drugim vremenom, mozda je jedna 10.10.2023. - 12.10.2023. 10-12h raf1, a drugi 11.10.2023. 10-12h raf1
    // to je i dalje preklaanje, isto i za vreme vazi
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

        //If time doesn't exist, this is for search when someone wants range od dates
        if (this.getStartTime() == null || that.getStartTime() == null || this.getEndTime() == null && that.getEndTime() == null) {
            //Only look for date when time doesn't exist
            return isDateEqual(that);
        }
        //Time exists

        //If They intersect with date and If they intersect with time they are same, else they are not the same
        return isDateEqual(that) && isBetweenTime(that);
    }

    //True - they intersect
    public boolean isBetweenTime(Time that) {

        //If their StartTime is the same
        if (this.getStartTime().getHour() == that.getStartTime().getHour() && this.getStartTime().getMinute() == that.getStartTime().getMinute())
            return true;

        //If their EndTime is the same
        if (this.getEndTime().getHour() == that.getEndTime().getHour() && this.getEndTime().getMinute() == that.getEndTime().getMinute())
            return true;

        //If THIS StartTime is between THAT StartTime and EndTime
        if (this.getStartTime().isAfter(that.getStartTime()) && this.getStartTime().isBefore(that.getEndTime()))
            return true;

        //If THIS EndTime is between THAT StartTime and EndTime
        if (this.getEndTime().isAfter(that.getStartTime()) && this.getEndTime().isBefore(that.getEndTime()))
            return true;

        //If THAT StartTime is between THIS StartTime and EndTime
        if (that.getStartTime().isAfter(this.getStartTime()) && that.getStartTime().isBefore(this.getEndTime()))
            return true;

        //If THAT EndTime is between THIS StartTime and EndTime
        if (that.getEndTime().isAfter(this.getStartTime()) && that.getEndTime().isBefore(this.getEndTime()))
            return true;

        return false;
    }

    //True - they intersect
    public boolean isDateEqual(Time that) {

        //If start dates and end dates are equal
        if (this.getStartDate().equals(that.getStartDate()) && this.getEndDate().equals(that.getEndDate()))
            return true;

        /*
        //If THIS StartDate is between THAT StartDate and EndDate
        if (this.getStartDate().isAfter(that.getStartDate()) && this.getStartDate().isBefore(that.getEndDate()))
            return true;

        //If THIS EndDate is between THAT StartDate and EndDate
        if (this.getEndDate().isAfter(that.getStartDate()) && this.getEndDate().isBefore(that.getEndDate()))
            return true;

        //If THAT StartDate is between THIS StartDate and EndDate
        if (that.getStartDate().isAfter(this.getStartDate()) && that.getStartDate().isBefore(this.getEndDate()))
            return true;

        //If THAT EndDate is between THIS StartDate and EndDate
        if (that.getEndDate().isAfter(this.getStartDate()) && that.getEndDate().isBefore(this.getEndDate()))
            return true;
         */

        return false;
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
                ", day=" + day +
                '}';
    }
}
