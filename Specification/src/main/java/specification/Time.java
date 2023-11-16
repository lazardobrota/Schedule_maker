package specification;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

//Represent set time, one instace of Time represent one row
@Getter
@Setter
public class Time {
    private Map<String, String> additionally = new HashMap<>();//additional columns

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

    /**
     * Make Time with additional information about that time, when its starting and ending
     * @param additionally additional information about the time
     * @param startDate starting Date of time
     * @param endDate ending Date of time
     * @param startTime starting Time of time
     * @param endTime ending Time of time
     */
    public Time(Map<String, String> additionally, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.additionally = additionally;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Without additional data(HashMap)
    /**
     * Without additional data make Time
     * @param startDate starting Date of time
     * @param endDate ending Date of time
     * @param startTime starting Time of time
     * @param endTime ending Time of time
     */
    public Time(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Time "from + how" much, meaning 10h + 2 == 10-12h

    /**
     * Make Time with additional information about that time, when its starting and how long it lasts
     * @param additionally additional information about the time
     * @param startDate starting Date of time
     * @param endDate ending Date of time
     * @param startTime starting Time of time
     * @param minutesToAdd how long it will last
     */
    public Time(Map<String, String> additionally, LocalDate startDate, LocalDate endDate, LocalTime startTime, long minutesToAdd) {
        this.additionally = additionally;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(minutesToAdd);// adds minuts to date to be "from to"
    }

    //Without addicional data(HashMap)

    /**
     * Without additional data make Time, how long it lasts
     * @param startDate starting Date of time
     * @param endDate ending Date of time
     * @param startTime starting Time of time
     * @param minutesToAdd how long it will last
     */
    public Time(LocalDate startDate, LocalDate endDate, LocalTime startTime, long minutesToAdd) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(minutesToAdd); // adds minuts to date to be "from to"
    }

    /**
     * Make empty Time class
     */
    public Time() {
    }

    //Clone atributes constructor

    /**
     * Clone Time class with different adress
     * @param that Time class that needs to be cloned
     */
    public Time(Time that) {
        this.additionally = that.getAdditionally();
        this.startDate = that.getStartDate();
        this.endDate = that.getEndDate();
        this.startTime = that.getStartTime();
        this.endTime = that.getEndTime();
        this.day = that.getDay();
    }

    /**
     * Check is two Time classes are equal
     * @param obj other Time class
     * @return boolean true if their dates and time intersect, so they are the same
     */
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

    /**
     * Does their time intersect
     * @param that other Time class
     * @return boolean true if they intersect
     */
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

    /**
     * Are their dates same
     * @param that other Time class
     * @return boolean true if they are same dates
     */
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
