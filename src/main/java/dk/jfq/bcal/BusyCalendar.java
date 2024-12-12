package dk.jfq.bcal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class BusyCalendar {

    private static final int NUMBER_OF_WEEKS = 4;

    public static void main(String[] args) throws IOException {
        final LocalDate firstMonday = getMondayInThisWeek();
        List<BusyWeek> busyWeeks = IntStream.range(0, NUMBER_OF_WEEKS).boxed().map(i -> new BusyWeek(firstMonday.plusDays(i * 7L), Duration.ofMinutes(15))).toList();

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(Paths.get("/Users/jfq/Downloads/jfq.ical").toFile()))) {
            try (BusyEventsReader busyEventsReader = new BusyEventsReader(in, firstMonday, firstMonday.plusDays(NUMBER_OF_WEEKS * 7))) {
                for (BusyEvent busyEvent : busyEventsReader) {
                    for (BusyWeek busyWeek : busyWeeks) {
                        busyWeek.add(busyEvent);
                    }
                }
            }
        }
        busyWeeks.forEach(System.out::println);
    }

    private static LocalDate getMondayInThisWeek() {
        LocalDate monday = LocalDate.now();
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        return monday;
    }
}
