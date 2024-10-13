package tacos.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.client.RequestDataFileClient;
import tacos.model.ClientRequestsData;
import tacos.model.RequestClientInfo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service logs the client info and may store it for statistics
 */
@AllArgsConstructor
@Slf4j
@Service
public class RequestAnalyzerService {
    private static final int MAX_REQUESTS_TO_SUPPLY = 10;

    @Autowired
    RequestDataFileClient requestDataFileClient;

    public void analyzeRequest(HttpServletRequest request) {
        RequestClientInfo requestClientInfo = new RequestClientInfo(request.getRemoteAddr(), request.getRemotePort(),
                ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Australia/Sydney"))); // Can also use AET
        requestDataFileClient.updateClientRequestsData(requestClientInfo);
    }

    public String listLastIPAddressAccesses() {
        ClientRequestsData clientRequestsData = requestDataFileClient.getClientRequestsData();
        List<RequestClientInfo> clientInfoList = clientRequestsData.getRequestClientInfoList();
        StringBuilder sb = new StringBuilder();
        sb.append("Information about the most recent request made to the Taco Cloud home page:\n");
        for(int i = 0; i < MAX_REQUESTS_TO_SUPPLY; i++) {
            sb.append(clientInfoList.get(clientInfoList.size() - 1 - i));
        }
        return sb.toString();
    }

    /**
     * Return some useful information about incoming requests
     * @return Data on client requests, containing a limited number of entries to keep request lightweight
     */
    public ClientRequestsData getFilteredClientRequestsData() {
        ClientRequestsData clientRequestsData = requestDataFileClient.getClientRequestsData();
        ArrayList<RequestClientInfo> clientInfoList = clientRequestsData.getRequestClientInfoList();
        ArrayList<RequestClientInfo> newList = new ArrayList<>();
        for(int i = 0; i < MAX_REQUESTS_TO_SUPPLY; i++) {
            newList.add(clientInfoList.get(clientInfoList.size() - 1 - i));
        }
        return new ClientRequestsData(newList);
    }

    public ArrayList<String> getFilteredClientRequestsDataAsStrings() {
        ClientRequestsData clientRequestsData = requestDataFileClient.getClientRequestsData();
        ArrayList<RequestClientInfo> clientInfoList = clientRequestsData.getRequestClientInfoList();
        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i < MAX_REQUESTS_TO_SUPPLY; i++) {
            newList.add(clientInfoList.get(clientInfoList.size() - 1 - i).toString());
        }
        return newList;
    }

//    public ClientRequestsData getFilteredClientRequestsDataStrings() {
//        ClientRequestsData clientRequestsData = requestDataFileClient.getClientRequestsData();
//        List<RequestClientInfo> clientInfoList = clientRequestsData.getRequestClientInfoList();
//        return new ClientRequestsData((ArrayList<RequestClientInfo>) clientInfoList.subList(0, 10));
//    }
}
