package tacos.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public record RequestClientInfo(String remoteIP, int remotePort, ZonedDateTime currentZonedDateTime) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------Request client info-------").append("\nClient IP: ").append(remoteIP)
        .append("\nClient port: ").append(remotePort)
        .append("\nTime received: ").append(currentZonedDateTime);
        return sb.toString();
    }
}
