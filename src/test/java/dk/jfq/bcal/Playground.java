package dk.jfq.bcal;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.message.StatusLine;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;

public class Playground {

    public static void main(String[] args) throws IOException {
        String icalUrl = System.getenv("BUSY_ICAL_URL");
        if (icalUrl == null || icalUrl.isEmpty()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("iCal url:");
            icalUrl = in.readLine();
        }
        try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build()) {
            HttpGet httpGet = new HttpGet(icalUrl);
            System.out.println("Downloading iCalender");
            BusyCalendar busyCalendar = closeableHttpClient.execute(httpGet, response -> {
                StatusLine statusLine = new StatusLine(response);
                if (statusLine.isSuccessful()) {
                    return new BusyCalendar(response.getEntity().getContent(), LocalDate.now(), 4, Duration.ofMinutes(15));
                } else {
                    throw new IOException(statusLine.toString());
                }
            });
            System.out.println(busyCalendar);
        }
    }
}
