package tacos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.client.RequestDataFileClient;
import tacos.model.ClientRequestsData;
import tacos.model.RequestClientInfo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
//        String lineToSave = String.join(",",
//                request.getRemoteAddr(),
//                request.getRemoteHost(),
//                String.valueOf(request.getRemotePort()),
//                ZonedDateTime.now().toString());
        RequestClientInfo requestClientInfo = new RequestClientInfo(request.getRemoteAddr(), request.getRemotePort(),
                ZonedDateTime.now());


//        requestDataFileClient.writeLineToFile(lineToSave);
//        requestDataFileClient.writeObjectToFile(new RequestClientInfo(request.getRemoteAddr(), request.getRemotePort(),
//                ZonedDateTime.now()));
//        ClientRequestsData clientsRequestData = requestDataFileClient.readObjectFromFile(); // TODO Fix
//        if (clientInfoList != null)
//            for(RequestClientInfo info : clientInfoList) {
//                System.out.println(info);
//            }
//        }
//        requestDataFileClient.writeLittleObjectToFile(new RequestClientInfo(request.getRemoteAddr(), request.getRemotePort(),
//                ZonedDateTime.now()));
//        log.info(requestDataFileClient.readLittleObjectFromFile().toString());
        requestDataFileClient.writeObjectToJsonFile(requestClientInfo);

    }
}
