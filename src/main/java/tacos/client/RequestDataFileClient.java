package tacos.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class RequestDataFileClient {
    private static String clientRequestDataDir = "Tacocloud/Data/ClientRequest";
    private FileSystem fileSystem;
    private Path requestDataDirPath;
    private Path requestDataFilePath;

    public RequestDataFileClient() {
        fileSystem = java.nio.file.FileSystems.getDefault();
        initClientRequestDataDir();
        initClientRequestDataFile();
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
            log.error("Could not create dataDirPath for client request info.", e);
        }
    }

    private void initClientRequestDataFile() {
        // check if file exists. if not, create new
        requestDataFilePath = requestDataDirPath.resolve("ClientRequestData.txt");
        if (Files.exists(requestDataFilePath)) {
            if (!Files.isRegularFile(requestDataFilePath)) {
                try {
                    Files.delete(requestDataFilePath);
                } catch (IOException e) {
                    log.error("Could not delete non-regular file.", e);
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

    public void saveDataToFile(String s) {
        try {
            if (Files.isWritable(requestDataDirPath)) {
                try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(requestDataFilePath.toFile(), true))) {
                    bufferedWriter.write(s);
                    bufferedWriter.newLine();
                    log.info("line written to file");
                }
            }
        } catch (IOException e) {
            log.error("Failed to write to file.", e);
        }
    }

}
