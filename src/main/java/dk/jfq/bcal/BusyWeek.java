package dk.jfq.bcal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

public class BusyWeek {

    private final LocalDate startOfWeek;

    private final Duration slotDuration;

    private final List<BusyDay> busyDays;

    public BusyWeek(LocalDate startOfWeek, Duration slotDuration) {
        this.startOfWeek = startOfWeek;
        this.slotDuration = slotDuration;
        this.busyDays = IntStream.range(0,7).boxed().map(i -> new BusyDay(startOfWeek.plusDays(i), slotDuration)).toList();
    }

    public void add(BusyEvent event) {
        busyDays.forEach(b -> b.add(event));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startOfWeek).append(" - ").append(startOfWeek.plusDays(6)).append('\n');
        for (LocalDateTime slotTime = LocalDateTime.of(startOfWeek, LocalTime.MIDNIGHT); slotTime.toLocalDate().equals(startOfWeek); slotTime = slotTime.plus(slotDuration)) {
            if (slotTime.toLocalTime().isBefore(LocalTime.of(6,0)) || slotTime.toLocalTime().isAfter(LocalTime.of(17,59))) {
                continue;
            }
            sb.append(slotTime.toLocalTime()).append(" - ").append(slotTime.toLocalTime().plus(slotDuration)).append(" |");
            for (BusyDay busyDay : busyDays) {
                sb.append(busyDay.isBusy(slotTime.toLocalTime()) ? " XXX |" : "     |");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
