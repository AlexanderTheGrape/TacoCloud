package tacos.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public record RequestClientInfo(String remoteIP, int remotePort, ZonedDateTime currentZonedDateTime) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private static final Locale locale = new Locale("en-AU");
    private static final ZoneId zoneId = ZoneId.of("Australia/Sydney");
    private static final DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
    private static final DateTimeFormatter amPmOfDayDateTimeFormatter = DateTimeFormatter.ofPattern("hh:mma EEE dd LLL uuuu").withLocale(locale);

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // ZonedDateTime is just an epoch (instant) value when serialized with Jacksonized. It needs to be provided with its time zone again.
        ZonedDateTime updatedZonedDateTime = currentZonedDateTime.withZoneSameInstant(zoneId);
        sb.append(updatedZonedDateTime.format(amPmOfDayDateTimeFormatter)).append(" ").append(zoneId)
                .append(" - from IP address ").append(remoteIP);
        return sb.toString();
    }
}
