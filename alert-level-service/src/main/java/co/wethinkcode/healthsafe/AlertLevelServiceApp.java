
package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.util.Map;

public class AlertLevelServiceApp {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7032);

        AlertLevelService alertService = new AlertLevelService();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/alert-level", ctx -> {
            ctx.json(Map.of("level", alertService.getLevel()));
        });

        app.post("/alert-level", ctx -> {
            try {
                Map body = ctx.bodyAsClass(Map.class);
                Object value = body.get("level");

                if (!(value instanceof Integer)) {
                    ctx.status(400).result("Level must be an integer");
                    return;
                }

                int level = (Integer) value;
                alertService.updateLevel(level);
                ctx.json(Map.of("level", alertService.getLevel()));

            } catch (IllegalArgumentException e) {
                ctx.status(400).result(e.getMessage());
            } catch (Exception e) {
                ctx.status(400).result("Invalid request body");
            }
        });

// TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
