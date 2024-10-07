package tacos.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.client.RequestDataFileClient;
import tacos.model.RequestClientInfo;

import java.time.ZonedDateTime;

/**
 * Service logs the client info and may store it for statistics
 */
@Slf4j
@Service
public class RequestAnalyzerService {
    @Autowired
    RequestDataFileClient requestDataFileClient;




    public RequestAnalyzerService() {

    }

    public void analyzeRequest(HttpServletRequest request) {
        RequestClientInfo requestClientInfo = new RequestClientInfo(request.getRemoteAddr(), request.getRemotePort(),
                ZonedDateTime.now());
        requestDataFileClient.updateClientRequestsData(requestClientInfo);
    }
}
