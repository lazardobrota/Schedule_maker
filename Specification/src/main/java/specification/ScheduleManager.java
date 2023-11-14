package specification;

public class ScheduleManager {

    private static Schedule schedule;
    public static void registerSchedule(Schedule schedule) { //setter
        ScheduleManager.schedule = schedule;
    }

    public static Schedule getSchedule() { //getter
        return schedule;
    }
}
