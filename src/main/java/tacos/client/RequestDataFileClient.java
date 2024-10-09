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
public class RequestDataFileClient extends AbstractFileClient{
    private final static String clientRequestDataDir = "Tacocloud/Data/ClientRequest";
    private final static String requestObjectDataFile = "ClientRequestObjectData.txt";
    private final static String requestDataFile = "ClientRequestData.ser";
    private final static String clientDataJsonFile = "ClientRequestData.json";
    private final static int MAX_REQUEST_CLIENT_INFO_LIST_SIZE = 1000;
    private Path requestDataDirPath;
    private Path requestDataFilePath;
    private Path requestObjectDataFilePath;
    private Path requestJsonDataFilePath;

    public RequestDataFileClient() {
        fileSystem = java.nio.file.FileSystems.getDefault();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initDirectoryForFiles();
        initFiles();
    }

    @Override
    protected void initDirectoryForFiles() {
        // check if dir exists/create needed dir
        Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
        Path root = null;
        boolean rootFound = false;
        while(!rootFound && rootDirectories.iterator().hasNext()) {
            if (root == null  || !Files.isReadable(root)) {
                root = rootDirectories.iterator().next();
                if (Files.isReadable(root)) {
                    rootFound = true;
                }
            }
        }
        if (root == null) {
            throw new RuntimeException("Writeable root directory of filesystem not found");
        }

        // ensure directory to store files exists
        requestDataDirPath = root.resolve(clientRequestDataDir);
        try {
            Files.createDirectories(requestDataDirPath);
        } catch (IOException e) {
            log.error("Could not create dataDirPath for client request info", e);
        }
    }

    @Override
    protected void initFiles() {
        requestDataFilePath = requestDataDirPath.resolve(requestDataFile);
        requestObjectDataFilePath = requestDataDirPath.resolve(requestObjectDataFile);
        requestJsonDataFilePath = requestDataDirPath.resolve(clientDataJsonFile);
        createFiles(requestDataFilePath, requestObjectDataFilePath, requestJsonDataFilePath);
    }

    private void createFiles(Path... paths) {
        for(Path path : paths) {
            // check if file exists. if not, create new
            if (!Files.exists(path)) {
                try {
                    Files.createFile(path);
                    log.info("File created: {}", path);
                } catch (IOException e) {
                    log.error("Could not create file", e);
                }
            } else {
                log.debug("File exists. It is located at: {}", path.toAbsolutePath());
            }
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

    public void writeClientInfoObjectToJavaSerializedFile(RequestClientInfo requestClientInfo) {
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
    }

    public ClientRequestsData getClientRequestsData() {
        return readObjectFromJsonFile(ClientRequestsData.class, requestJsonDataFilePath);
    }
}
