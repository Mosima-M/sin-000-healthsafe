package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

    public class IngestionClient {

        private final HttpClient client = HttpClient.newHttpClient();

        private static final String INGESTION_URL =
                "http://localhost:7030";

        public String getAllWards() throws IOException, InterruptedException {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INGESTION_URL + "/wards"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new IOException(
                        "Ingestion Service returned " + response.statusCode()
                );
            }

            return response.body();
        }
    }

