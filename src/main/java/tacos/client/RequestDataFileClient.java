package tacos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tacos.model.ClientRequestsData;
import tacos.model.RequestClientInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@Slf4j
@Service
public class RequestDataFileClient extends AbstractJsonFileClient {
    private final static String clientRequestDataDir = "Tacocloud/Data/ClientRequest";
    private final static String requestObjectDataFile = "ClientRequestObjectData.txt";
    private final static String requestDataFile = "ClientRequestData.ser";
    private final static String clientDataJsonFile = "ClientRequestData.json";
    private final static int MAX_REQUEST_CLIENT_INFO_LIST_SIZE = 1000;
    private Path requestDataFilePath;
    private Path requestObjectDataFilePath;
    private Path requestJsonDataFilePath;

    public RequestDataFileClient() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initDirectoryForFiles(clientRequestDataDir);
        initFiles();
    }

    protected void initFiles() {
        requestDataFilePath = initFile(requestDataFile);
        requestObjectDataFilePath = initFile(requestObjectDataFile);
        requestJsonDataFilePath = initFile(clientDataJsonFile);
    }


    public void writeLineToFile(String s) {
        try {
            if (Files.isWritable(fileDirectory)) {
                try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(requestDataFilePath.toFile()))) {
                    bufferedWriter.write(s);
                    bufferedWriter.newLine();
                    log.info("line written to file");
                }
            }
        } catch (IOException e) {
            log.error("Failed to write to file", e);
        }
    }

    public void writeClientInfoObjectToJavaSerializedFile(RequestClientInfo requestClientInfo) {
        try {
            if (Files.isWritable(fileDirectory)) {
                try(FileOutputStream fos = new FileOutputStream(requestObjectDataFilePath.toFile(), false);
                    ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(requestClientInfo);
                }
            } else {
                log.error("File not writable");
            }
        } catch (IOException e) {
            log.error("Failed to write to file", e);
        }
    }

    public RequestClientInfo readClientInfoObjectFromToJavaSerializedFile() {
        RequestClientInfo data = null;
        try {
            if (Files.isReadable(requestObjectDataFilePath)) {
                try (FileInputStream fis = new FileInputStream(requestObjectDataFilePath.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis)) {
                    data = (RequestClientInfo) ois.readObject();
                } catch (ClassNotFoundException e) {
                    log.error("Class not found for object deserialization", e);
                }
            } else {
                log.error("File not readable");
            }
        } catch (IOException e) {
            log.error("Failed to read objects from file", e);
        }
        return data;
    }

    public void updateClientRequestsData(RequestClientInfo singleRequestInfo) {
        try {
            ClientRequestsData data = readObjectFromJsonFile(ClientRequestsData.class, requestJsonDataFilePath);
            if (data == null) {
                ArrayList<RequestClientInfo> requestsList = new ArrayList<>();
                data = new ClientRequestsData(requestsList);
            }

            ArrayList<RequestClientInfo> requestsList = data.getRequestClientInfoList();
            if (requestsList.size() >= MAX_REQUEST_CLIENT_INFO_LIST_SIZE) {
                requestsList.remove(0);
            }
            requestsList.add(singleRequestInfo);
            data.setRequestClientInfoList(requestsList);

            writeObjectToJsonFile(data, requestJsonDataFilePath);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public ClientRequestsData getClientRequestsData() {
        return readObjectFromJsonFile(ClientRequestsData.class, requestJsonDataFilePath);
    }
}
