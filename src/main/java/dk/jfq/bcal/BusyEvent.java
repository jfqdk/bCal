package dk.jfq.bcal;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class BusyEvent implements  Comparable<BusyEvent> {

    private LocalDateTime start;

    private LocalDateTime end;

    private String summary;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Event{" +
                "start=" + (start != null ? ISO_LOCAL_DATE_TIME.format(start) : "NULL") +
                ", end=" + (end != null ? ISO_LOCAL_DATE_TIME.format(end) : "NULL") +
                ", summary='" + summary + '\'' +
                '}';
    }

    @Override
    public int compareTo(BusyEvent o) {
        if (start == null && o.start == null) {
            return Integer.compare(this.hashCode(), o.hashCode());
        } else if (start == null) {
            return -1;
        } else if (o.end == null) {
            return 1;
        }
        return start.compareTo(o.start);
    }
}
