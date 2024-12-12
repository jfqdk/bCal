package dk.jfq.bcal;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.io.TimezoneInfo;
import biweekly.io.text.ICalReader;
import biweekly.property.ValuedProperty;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class BusyEventsReader implements AutoCloseable, Iterable<BusyEvent> {

    private final InputStream iCalendarStream;

    private final SortedSet<BusyEvent> busyEvents = new TreeSet<>();

    public BusyEventsReader(InputStream iCalendarStream, LocalDate from, LocalDate to) throws IOException {
        Date start = Date.from(LocalDateTime.of(from, LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant());
        Instant end = LocalDateTime.of(to, LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        this.iCalendarStream = iCalendarStream;

        ICalReader iCalReader = new ICalReader(iCalendarStream);
        for (ICalendar iCalendar = iCalReader.readNext(); iCalendar != null; iCalendar = iCalReader.readNext()) {
            TimezoneInfo timezoneInfo = iCalendar.getTimezoneInfo();
            for (VEvent vEvent : iCalendar.getEvents()) {
                TimeZone timezone = getTimeZone(vEvent, timezoneInfo);
                DateIterator it = vEvent.getDateIterator(timezone);
                it.advanceTo(start);
                while (it.hasNext()) {
                    Date next = it.next();
                    if (next.toInstant().isAfter(end)) {
                        break;
                    }
                    BusyEvent busyEvent = toEvent(vEvent, next);
                    if (busyEvent != null) {
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

    @Override
    public void close() {
        try {
            iCalendarStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<BusyEvent> iterator() {
        return busyEvents.iterator();
    }
}
