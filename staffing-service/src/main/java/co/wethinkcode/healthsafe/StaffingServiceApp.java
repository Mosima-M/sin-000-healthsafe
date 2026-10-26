
package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import java.util.Map;

public class StaffingServiceApp {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7033);

        WardClient wardClient = new WardClient();
        AlertLevelClient alertLevelClient = new AlertLevelClient();

        StaffingService staffingService =
                new StaffingService(wardClient, alertLevelClient);
        StaffingEventPublisher publisher = new StaffingEventPublisher();
        ObjectMapper mapper = new ObjectMapper();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/staffing/{wardId}", ctx -> {
            try {
                String wardId = ctx.pathParam("wardId");

                Map<String, Object> schedule =
                        staffingService.getSchedule(wardId);

                if (schedule == null) {
                    ctx.status(404).result("Ward not found");
                    return;
                }

                String message = mapper.writeValueAsString(schedule);
                publisher.publish(message);

                ctx.json(schedule);


            } catch (Exception e) {
                ctx.status(502).result("Dependent service unavailable");
            }
        });
    }
}


// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
