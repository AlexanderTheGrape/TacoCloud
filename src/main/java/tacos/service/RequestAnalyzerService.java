package tacos.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.client.RequestDataFileClient;

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
        String lineToSave = String.join(",",
                request.getRemoteAddr(),
                request.getRemoteHost(),
                String.valueOf(request.getRemotePort()),
                ZonedDateTime.now().toString());

        requestDataFileClient.saveDataToFile(lineToSave);
    }
}
