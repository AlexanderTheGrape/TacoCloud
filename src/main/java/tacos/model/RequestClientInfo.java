package tacos.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RequestClientInfo implements Serializable {
    private String remoteIP;
    private int remotePort;
    private ZonedDateTime currentZonedDateTime;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------Request client info-------");
        sb.append("\nRemote IP: " + remoteIP);
        sb.append("\nRemote port: " + remotePort);
        sb.append("\nTime received: " + currentZonedDateTime);
        return sb.toString();
    }
}
