package tacos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tacos.model.ClientRequestsData;
import tacos.model.RequestClientInfo;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RequestDataFileClient {
    private final static String clientRequestDataDir = "Tacocloud/Data/ClientRequest";
    private final static String requestObjectDataFile = "ClientRequestObjectData.txt";
    private final static String requestDataFile = "ClientRequestData.ser";
    private final static String clientDataJsonFile = "ClientRequestData.json";
    private FileSystem fileSystem;
    private Path requestDataDirPath;
    private Path requestDataFilePath;
    private Path requestObjectDataFilePath;
    private Path requestJsonDataFilePath;
    private ObjectMapper objectMapper;

    public RequestDataFileClient() {
        fileSystem = java.nio.file.FileSystems.getDefault();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initClientRequestDataDir();
        initClientRequestDataFiles();
//        initClientRequestFileOutputStreams();
    }

    private void initClientRequestDataDir(){
        // check if dir exists/create needed dir
        Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
        Path root;
        if (rootDirectories.iterator().hasNext()) {
            root = rootDirectories.iterator().next();
        } else {
            log.error("Root directory of filesystem not found when saving client request info");
            return;
        }
        log.info("Root is: {}", root);

        // ensure file dir exists
        requestDataDirPath = root.resolve(clientRequestDataDir);
        try {
            Files.createDirectories(requestDataDirPath);
        } catch (IOException e) {
            log.error("Could not create dataDirPath for client request info", e);
        }
    }

    private void initClientRequestDataFiles() {
        // check if file exists. if not, create new
        requestDataFilePath = requestDataDirPath.resolve(requestDataFile);
        requestObjectDataFilePath = requestDataDirPath.resolve(requestObjectDataFile);
        requestJsonDataFilePath = requestDataDirPath.resolve(clientDataJsonFile);
//        if (!Files.exists(requestDataFilePath)) {
//            try {
//                Files.delete(requestDataFilePath);
//                Files.createFile(requestDataFilePath);
//            } catch (IOException e) {
//                log.error("Could not delete non-regular file", e);
//            }
//        }
        if (!Files.exists(requestJsonDataFilePath)) {
            try {
                Files.createFile(requestJsonDataFilePath);
                log.info("json file created");
            } catch (IOException e) {
                log.error("Could not delete non-regular file", e);
            }
        } else {
            log.info("json file exists. It is located at: {}", requestJsonDataFilePath.toAbsolutePath());
        }
    }

    public void writeLineToFile(String s) {
        try {
            if (Files.isWritable(requestDataDirPath)) {
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

    public void writeObjectToFile(RequestClientInfo requestClientInfo) { // TODO make generic
        try {
            if (Files.isWritable(requestDataDirPath)) {
                try(FileOutputStream fos = new FileOutputStream(requestObjectDataFilePath.toFile(), false);
                        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    ClientRequestsData data = readObjectFromFile();
                    if (data != null && !data.getRequestClientInfoList().isEmpty()) {
                        ArrayList<RequestClientInfo> clientInfoList = data.getRequestClientInfoList();
                        clientInfoList.add(new RequestClientInfo(
                                requestClientInfo.getRemoteIP(),
                                requestClientInfo.getRemotePort(),
                                requestClientInfo.getCurrentZonedDateTime()));
                        data.setRequestClientInfoList(clientInfoList);
                    } else {
                        log.info("Client info list was empty. Creating a new object.");
                        ArrayList<RequestClientInfo> clientInfoList = new ArrayList<>();
                        clientInfoList.add(new RequestClientInfo(
                                requestClientInfo.getRemoteIP(),
                                requestClientInfo.getRemotePort(),
                                requestClientInfo.getCurrentZonedDateTime()));
                        data = new ClientRequestsData(clientInfoList);
                    }
                    log.info("Data object after reading and updating: {}", data);
                    oos.writeObject(data);
                }
            } else {
                log.error("File not writable");
            }
        } catch (IOException e) {
            log.error("Failed to write to file", e);
        }
    }

    public void writeLittleObjectToFile(RequestClientInfo requestClientInfo) { // TODO make generic
        try {
            if (Files.isWritable(requestDataDirPath)) {
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

    public RequestClientInfo readLittleObjectFromFile() { // TODO make generic, use wildcards/super if possible
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

    public ClientRequestsData readObjectFromFile() { // TODO make generic, use wildcards/super if possible
        ClientRequestsData data = null;
        try {
            if (Files.isReadable(requestObjectDataFilePath)) {
                try (FileInputStream fis = new FileInputStream(requestObjectDataFilePath.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)) {
                    data = (ClientRequestsData) ois.readObject();
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

    public <T> void writeObjectToJsonFile(T data, Path filePath) {
        try {
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            log.error("IOException when writing object to json file", e);
        }
    }

    public <T> T readObjectFromJsonFile(Class<T> type, Path filePath) {
        T obj = null;
        try {
            obj = objectMapper.readValue(filePath.toFile(), type);
        } catch (IOException e) {
            log.error("IOException when writing object to json file", e);
        }
        return obj;
    }

    public void updateClientRequestsData(RequestClientInfo singleRequestInfo) {
        ClientRequestsData data = readObjectFromJsonFile(ClientRequestsData.class, requestJsonDataFilePath);
        if (data == null) {
            ArrayList<RequestClientInfo> requestsList = new ArrayList<>();
            data = new ClientRequestsData(requestsList);
        }

        ArrayList<RequestClientInfo> requestsList = data.getRequestClientInfoList();
        requestsList.add(singleRequestInfo);
        data.setRequestClientInfoList(requestsList);

        writeObjectToJsonFile(data, requestJsonDataFilePath);
    }


}
