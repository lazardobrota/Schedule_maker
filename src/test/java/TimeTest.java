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
        Time time = new Time(LocalDate.now(), LocalTime.now(), 120);
        LocalTime test = LocalTime.now().plus(Duration.of(120, ChronoUnit.MINUTES));
        assertEquals(test.getHour(), time.getEndTime().getHour());
    }
}
