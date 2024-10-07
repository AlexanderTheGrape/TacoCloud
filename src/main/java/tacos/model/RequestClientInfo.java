package tacos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestClientInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
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
