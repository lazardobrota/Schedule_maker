import org.junit.jupiter.api.Test;
import specification.Time;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTest {

    //Check if minutsToAdd works
    @Test
    public void checkToDate() {
        Time time = new Time(LocalDate.now(), LocalDate.now(), LocalTime.now(), 120);
        LocalTime test = LocalTime.now().plusMinutes(120);
        assertEquals(test.getHour(), time.getEndTime().getHour());
    }

    @Test
    public void areTimeClassesSame() {
        Time time = new Time(LocalDate.now(), LocalDate.now(), LocalTime.now(), 120);
        Time time2 = new Time(LocalDate.now(), LocalDate.now(), LocalTime.now(), 120);

        assertEquals(time, time2);

        //time.setDate(LocalDate.now().minusDays(1));
        //assertEquals(time, time2);
    }
}
