package tacos.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    private static String clientRequestDataDir = "Tacocloud/Data/ClientRequest";
    private FileSystem fileSystem;
    private Path requestDataDirPath;
    private Path requestDataFilePath;
    private Path requestObjectDataFilePath;

    public RequestDataFileClient() {
        fileSystem = java.nio.file.FileSystems.getDefault();
        initClientRequestDataDir();
        initClientRequestDataFiles();
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
        requestDataFilePath = requestDataDirPath.resolve("ClientRequestData.txt");
        requestObjectDataFilePath = requestDataDirPath.resolve("ClientRequestObjectData.txt");
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

    public void writeLineToFile(String s) {
        try {
            if (Files.isWritable(requestDataDirPath)) {
                try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(requestDataFilePath.toFile(), true))) {
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
                try(FileOutputStream fos = new FileOutputStream(requestObjectDataFilePath.toFile(), true);
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

    public List<RequestClientInfo> readObjectsFromFile() { // TODO make generic, use wildcards/super? etc if possible
        try {
            if (Files.isReadable(requestObjectDataFilePath)) {
                List<RequestClientInfo> clientInfoList = new ArrayList<>();
                try (FileInputStream fis = new FileInputStream(requestObjectDataFilePath.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)) {

                    while (true) {
                        RequestClientInfo requestClientInfo = (RequestClientInfo) ois.readObject();
                        if (requestClientInfo == null)
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class not found for object deserialization", e);
                } catch (StreamCorruptedException e) {
                    log.error("Stream corrupted", e); // TODO FIX the exceptions occurring
                }
                return clientInfoList;
            } else {
                log.error("File not readable");
                return null;
            }
        } catch (IOException e) {
            log.error("Failed to read objects from file", e);
            return null;
        }
    }

}
