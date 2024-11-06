package tacos.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static tacos.Constants.EN_AU_LOCALE;
import static tacos.Constants.ZONE_ID_SYDNEY;

public record RequestClientInfo(String remoteIP, int remotePort, ZonedDateTime currentZonedDateTime) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private static final DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(EN_AU_LOCALE);
    private static final DateTimeFormatter amPmOfDayDateTimeFormatter = DateTimeFormatter.ofPattern("hh:mma EEE dd LLL uuuu").withLocale(EN_AU_LOCALE);

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // ZonedDateTime is just an epoch (instant) value when serialized with Jacksonized. It needs to be provided with its time zone again.
        ZonedDateTime updatedZonedDateTime = currentZonedDateTime.withZoneSameInstant(ZONE_ID_SYDNEY);
        sb.append("(AET) ").append(updatedZonedDateTime.format(amPmOfDayDateTimeFormatter))
                .append(" - from IP address ").append(remoteIP);
        return sb.toString();
    }
}
