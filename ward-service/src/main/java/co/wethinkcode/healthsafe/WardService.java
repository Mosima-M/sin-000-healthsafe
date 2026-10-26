
package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

public class WardService {

    private final IngestionClient ingestionClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public WardService(IngestionClient ingestionClient) {
        this.ingestionClient = ingestionClient;
    }

    public Optional<JsonNode> getWardById(String wardId)
            throws IOException, InterruptedException {

        String json = ingestionClient.getAllWards();

        JsonNode wards = mapper.readTree(json);

        for (JsonNode ward : wards) {
            if (ward.path("wardId").asText().equalsIgnoreCase(wardId)) {
                return Optional.of(ward);
            }
        }

        return Optional.empty();
    }
}
