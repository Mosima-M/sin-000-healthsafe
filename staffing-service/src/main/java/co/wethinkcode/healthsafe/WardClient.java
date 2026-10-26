
package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WardClient {

    private final HttpClient client = HttpClient.newHttpClient();

    public boolean wardExists(String wardId)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7031/wards/" + wardId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            return false;
        }

        if (response.statusCode() != 200) {
            throw new IOException("Ward Service unavailable");
        }

        return true;
    }
}

