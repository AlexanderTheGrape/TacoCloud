package tacos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public abstract class AbstractJsonFileClient extends AbstractFileClient {
    protected ObjectMapper objectMapper;

    public <T> void writeObjectToJsonFile(T data, Path filePath) {
        try {
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            log.error("IOException when writing object to json file", e);
        }
    }

    public <T> T readObjectFromJsonFile(Class<T> type, Path filePath) { // TODO try U extends T
        T obj = null;
        try {
            obj = objectMapper.readValue(filePath.toFile(), type);
        } catch (IOException e) {
            log.error("IOException when writing object to json file", e);
        }
        return obj;
    }
}
