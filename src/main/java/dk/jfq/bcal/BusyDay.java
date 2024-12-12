package dk.jfq.bcal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

class BusyDay {

    private final Duration slotDuration;

    private final LocalDate day;

    private final SortedSet<LocalTime> busyTimeSlots = new TreeSet<>();

    public BusyDay(LocalDate day, Duration slotDuration) {
        this.day = day;
        this.slotDuration = slotDuration;
    }

    public void add(BusyEvent busyEvent) {
        LocalTime eventStartTime;
        LocalTime eventEndTime;
        if (busyEvent.getStart().toLocalDate().equals(day) && busyEvent.getEnd().toLocalDate().equals(day)) {
            // Starter og slutter i dag
            eventStartTime = busyEvent.getStart().toLocalTime();
            eventEndTime = busyEvent.getEnd().toLocalTime();
        } else if (busyEvent.getStart().toLocalDate().equals(day) && busyEvent.getEnd().toLocalDate().isAfter(day)) {
            // Starter i dag og slutter efter i dag
            eventStartTime = busyEvent.getStart().toLocalTime();
            eventEndTime = LocalTime.MIDNIGHT.minusSeconds(1);
        } else if (busyEvent.getStart().toLocalDate().isBefore(day) && busyEvent.getEnd().toLocalDate().equals(day)) {
            // Startede tidligere end i dag men slutter i dag
            eventStartTime = LocalTime.MIDNIGHT;
            eventEndTime = busyEvent.getEnd().toLocalTime();
        } else if (busyEvent.getStart().toLocalDate().isBefore(day) && busyEvent.getEnd().toLocalDate().isAfter(day)) {
            // Starter inden i dag og slutter efter i dag (dvs hele dagen)
            eventStartTime = LocalTime.MIDNIGHT;
            eventEndTime = LocalTime.MIDNIGHT.minusSeconds(1);
        } else {
            // Ligger ikke i dag
            return;
        }

        for (LocalDateTime slotTime = LocalDateTime.of(day, LocalTime.MIDNIGHT); slotTime.toLocalDate().equals(day) && slotTime.toLocalTime().isBefore(eventEndTime); slotTime = slotTime.plus(slotDuration)) {
            if (slotTime.toLocalTime().equals(eventStartTime) || slotTime.toLocalTime().isAfter(eventStartTime) || slotTime.toLocalTime().plus(slotDuration).isAfter(eventStartTime)) {
                busyTimeSlots.add(slotTime.toLocalTime());
            }
        }
    }

    public boolean isBusy(LocalTime slot) {
        return busyTimeSlots.contains(slot);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LocalDateTime slotTime = LocalDateTime.of(day, LocalTime.MIDNIGHT); slotTime.toLocalDate().equals(day); slotTime = slotTime.plus(slotDuration)) {
            sb.append(slotTime.toLocalDate())
                    .append(' ')
                    .append(slotTime.toLocalTime())
                    .append('-')
                    .append(slotTime.toLocalTime().plus(slotDuration))
                    .append(':')
                    .append(busyTimeSlots.contains(slotTime.toLocalTime()) ? " XXX" : "    ")
                    .append('\n');
        }
        return sb.toString();
    }
}
