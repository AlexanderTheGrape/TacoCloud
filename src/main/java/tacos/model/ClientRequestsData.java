package tacos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestsData implements Serializable {
    private static final long serialVersionUID = 1;
    private ArrayList<RequestClientInfo> requestClientInfoList;

    public String toString() {
        return requestClientInfoList.toString();
    }
}
