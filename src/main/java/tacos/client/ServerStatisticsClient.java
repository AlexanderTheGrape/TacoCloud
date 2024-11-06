package tacos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class ServerStatisticsClient extends AbstractJsonFileClient {
    private final String serverStatsDataDir = "Tacocloud/Data/ServerStats";
    private final String serverStatsDataJsonFile = "ServerStatsData.json";
    private Path serverStatsPath;

    public ServerStatisticsClient() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initDirectoryForFiles(serverStatsDataDir);
        initFiles();
    }

    protected void initFiles() {
        serverStatsPath = initFile(serverStatsDataJsonFile);
    }
}
