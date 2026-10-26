
package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import io.javalin.Javalin;

public class WardServiceApp {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7031);

        IngestionClient ingestionClient = new IngestionClient();
        WardService wardService = new WardService(ingestionClient);

        app.get("/health", ctx -> ctx.result("OK"));


        app.get("/wards/{wardId}", ctx -> {
            try {
                String wardId = ctx.pathParam("wardId");

                Optional<JsonNode> ward = wardService.getWardById(wardId);
                if (ward.isPresent()) {
                    ctx.json(ward.get());
                } else {
                    ctx.status(404).result("Ward not found");
                }

            } catch (Exception e) {
                ctx.status(502)
                        .result("Ingestion Service unavailable");
            }
        });

    }
}


// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
