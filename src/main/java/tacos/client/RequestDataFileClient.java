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
//    private FileOutputStream fos;
//    private ObjectOutputStream oos;

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
                    ArrayList<RequestClientInfo> clientInfoList = readObjectsFromFile();
                    System.out.println("list size after deserialisation: " + clientInfoList.size());
//                    log.info("\nClientInfoList after readObjectsFromFile:" + clientInfoList);
//                    System.out.println(clientInfoList);
                    if (!clientInfoList.isEmpty()) {
//                        log.info("\nClientInfoList was not empty. Before adding, its contents are:");
//                        System.out.println(clientInfoList);
                        clientInfoList.add(new RequestClientInfo(
                                    requestClientInfo.getRemoteIP(),
                                    requestClientInfo.getRemotePort(),
                                    requestClientInfo.getCurrentZonedDateTime()));
//                        log.info("\nAfter adding its contents now are:");
//                        System.out.println(clientInfoList);
                    } else {
                        log.info("\nclientInfoList was empty. Creating a new list.");
                        clientInfoList = new ArrayList<>();
                        clientInfoList.add(requestClientInfo);
                    }
                    log.info("calling oos.writeObject(x)");
                    oos.writeObject(clientInfoList);
                }
            } else {
                log.error("File not writable");
            }
        } catch (IOException e) {
            log.error("Failed to write to file", e);
        }
    }

    public ArrayList<RequestClientInfo> readObjectsFromFile() { // TODO make generic, use wildcards/super? etc if possible
        try {
            if (Files.isReadable(requestObjectDataFilePath)) {
                ArrayList<RequestClientInfo> clientInfoList = new ArrayList<>();
                try (FileInputStream fis = new FileInputStream(requestObjectDataFilePath.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)) {

                    while (true) {
                        ArrayList<RequestClientInfo> readObject = (ArrayList) ois.readObject();
//                        System.out.println("The read object is:");
//                        System.out.println(clientInfoList);
                        if (readObject == null) {
                            log.info("Read object evaluated as null");
                            break;
                        }
                        log.info("Object read from file. Its contents are: {}", readObject);
                        clientInfoList = readObject;
                    }
                } catch (EOFException e) {
                    // Do nothing, end of file reached
                } catch (ClassNotFoundException e) {
                    log.error("Class not found for object deserialization", e);
                }
                log.info("XXXXXX Right before returning the read clientInfoList, it is: {}", clientInfoList);
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
