
package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

public class WardServiceApp {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7031);

        IngestionClient ingestionClient = new IngestionClient();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> {
            try {
                String wards = ingestionClient.getAllWards();

                ctx.contentType("application/json");
                ctx.result(wards);

            } catch (Exception e) {
                ctx.status(502)
                        .result("Ingestion Service unavailable");
            }
        });
    }
}


// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
