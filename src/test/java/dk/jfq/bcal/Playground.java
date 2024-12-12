package dk.jfq.bcal;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.io.TimezoneInfo;
import biweekly.io.text.ICalReader;
import biweekly.property.ValuedProperty;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class Playground {

    @Test
    public void play() throws IOException {
        Instant from = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        Instant to = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        SortedSet<BusyEvent> busyEvents = new TreeSet<>();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(Paths.get("/Users/jfq/Downloads/jfq.ical").toFile()))) {
            ICalReader iCalReader = new ICalReader(in);
            for (ICalendar iCalendar = iCalReader.readNext(); iCalendar != null; iCalendar = iCalReader.readNext()) {
                TimezoneInfo timezoneInfo = iCalendar.getTimezoneInfo();
                for (VEvent vEvent : iCalendar.getEvents()) {
                    TimeZone timezone = getTimeZone(vEvent, timezoneInfo);
                    DateIterator it = vEvent.getDateIterator(timezone);
                    it.advanceTo(Date.from(from));
                    while (it.hasNext()) {
                        Date next = it.next();
                        if (next.toInstant().isAfter(to)) {
                            break;
                        }
                        BusyEvent busyEvent = toEvent(vEvent, next);
                        busyEvents.add(busyEvent);
                    }
                }
            }
        }
    }

    private static TimeZone getTimeZone(VEvent vEvent, TimezoneInfo timezoneInfo) {
        TimeZone timezone;
        if (timezoneInfo.isFloating(vEvent.getDateStart())) {
            timezone = TimeZone.getDefault();
        } else {
            TimezoneAssignment timezoneAssignment = timezoneInfo.getTimezone(vEvent.getDateStart());
            if (timezoneAssignment != null) {
                timezone = timezoneAssignment.getTimeZone();
            } else {
                timezone = TimeZone.getTimeZone("UTC");
            }
        }
        return timezone;
    }

    private BusyEvent toEvent(VEvent vEvent, Date dateStart) {
        if (vEvent == null) {
            return null;
        }
        String transparency = getValue(vEvent.getTransparency());
        if (transparency != null && transparency.equals("TRANSPARENT")) {
            return null;
        }
        String summary = getValue(vEvent.getSummary());
        if (summary == null || summary.trim().isEmpty()) {
            return null;
        }
        Date eventDateStart = getValue(vEvent.getDateStart());
        if (eventDateStart == null) {
            return null;
        }
        Date eventDateEnd = getValue(vEvent.getDateEnd());
        if (eventDateEnd == null) {
            return null;
        }

        BusyEvent busyEvent = new BusyEvent();
        busyEvent.setSummary(summary);
        busyEvent.setStart(LocalDateTime.ofInstant(dateStart.toInstant(), ZoneId.systemDefault()));
        busyEvent.setEnd(busyEvent.getStart().plus(Duration.between(eventDateStart.toInstant(), eventDateEnd.toInstant())));
        busyEvent.setSummary(summary);

        return busyEvent;
    }

    public LocalDateTime atLocaleZone(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public <V> V getValue(ValuedProperty<V> valuedProperty) {
        return valuedProperty != null ? valuedProperty.getValue() : null;
    }

}
