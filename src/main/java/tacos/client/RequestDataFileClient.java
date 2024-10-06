package tacos.client;

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
    private FileSystem fileSystem;
    private Path requestDataDirPath;
    private Path requestDataFilePath;
    private Path requestObjectDataFilePath;

    public RequestDataFileClient() {
        fileSystem = java.nio.file.FileSystems.getDefault();
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
        if (Files.exists(requestDataFilePath)) {
            if (!Files.isRegularFile(requestDataFilePath)) {
                try {
                    Files.delete(requestDataFilePath);
                } catch (IOException e) {
                    log.error("Could not delete non-regular file", e);
                }
            }
        } else {
            try {
                Files.createFile(requestDataFilePath);
            } catch(IOException e) {
                log.error("Could not create file", e);
            }
        }
    }

//    private void initClientRequestFileOutputStreams() {
//        try {
//            fos = new FileOutputStream(requestObjectDataFilePath.toFile(), true);
//            oos = new ObjectOutputStream(fos);
//        } catch(Exception e) {
//            log.error("Could not create file output stream", e);
//        }
//    }

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

}
