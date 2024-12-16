package dk.jfq.bcal;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class BusyCalendar {

    private final List<BusyWeek> busyWeeks;

    public BusyCalendar(InputStream iCalendarStream, LocalDate from, int numberOfWeeks, Duration slotDuration) throws IOException {
        LocalDate monday = getMondayInWeek(from);

        this.busyWeeks = IntStream.range(0, numberOfWeeks).boxed().map(i -> new BusyWeek(monday.plusDays(i * 7L), slotDuration)).toList();

        try (BusyEventsReader busyEventsReader = new BusyEventsReader(iCalendarStream, monday, monday.plusDays(numberOfWeeks * 7L))) {
            for (BusyEvent busyEvent : busyEventsReader) {
                for (BusyWeek busyWeek : busyWeeks) {
                    busyWeek.add(busyEvent);
                }
            }
        }
    }

    private static LocalDate getMondayInWeek(LocalDate weekDay) {
        LocalDate monday = weekDay;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        return monday;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BusyWeek busyWeek : busyWeeks) {
            sb.append(busyWeek).append('\n').append('\n');
        }
        return sb.toString();
    }
}
