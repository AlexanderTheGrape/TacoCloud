package tacos.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class AbstractFileClient {
    protected FileSystem fileSystem;
    protected Path fileDirectory;

    protected abstract void initFiles();

    protected Path initFile(String pathString) {
        Path path = fileDirectory.resolve(pathString);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
                log.debug("File created: {}", path);
            } catch (IOException e) {
                log.error("Could not create file", e);
            }
        } else {
            log.debug("File exists. It is located at: {}", path.toAbsolutePath());
        }
        return path;
    }

    protected void initDirectoryForFiles(String dataDir) {
        fileSystem = java.nio.file.FileSystems.getDefault();

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
        Path dataDirPath = root.resolve(dataDir);
        try {
            Files.createDirectories(dataDirPath);
        } catch (IOException e) {
            log.error("Could not create dataDirPath for client request info", e);
        }
        this.fileDirectory = dataDirPath;
    }
}
