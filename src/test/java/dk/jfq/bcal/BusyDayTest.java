package dk.jfq.bcal;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BusyDayTest {

    @Test
    public void test() {
        BusyEvent simpleEvent = new BusyEvent();
        simpleEvent.setStart(LocalDateTime.of(LocalDate.now(), LocalTime.of(12,52)));
        simpleEvent.setEnd(simpleEvent.getStart().plusHours(1));

        BusyEvent startsBeforeTodayEvent = new BusyEvent();
        startsBeforeTodayEvent.setStart(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(22,52)));
        startsBeforeTodayEvent.setEnd(startsBeforeTodayEvent.getStart().plusHours(4));

        BusyEvent endsAfterTodayEvent = new BusyEvent();
        endsAfterTodayEvent.setStart(LocalDateTime.of(LocalDate.now(), LocalTime.of(21,52)));
        endsAfterTodayEvent.setEnd(endsAfterTodayEvent.getStart().plusHours(5));

        BusyDay today = new BusyDay(LocalDate.now(), Duration.ofMinutes(15));
        today.add(simpleEvent);
        today.add(startsBeforeTodayEvent);
        today.add(endsAfterTodayEvent);
    }
}
