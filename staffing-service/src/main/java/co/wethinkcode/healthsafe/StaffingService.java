
package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.util.Map;

public class StaffingService {

    private final WardClient wardClient;
    private final AlertLevelClient alertLevelClient;

    public StaffingService(WardClient wardClient,
                           AlertLevelClient alertLevelClient) {
        this.wardClient = wardClient;
        this.alertLevelClient = alertLevelClient;
    }

    public Map<String, Object> getSchedule(String wardId)
            throws IOException, InterruptedException {

        if (!wardClient.wardExists(wardId)) {
            return null;
        }

        int level = alertLevelClient.getAlertLevel();
        int doctorsRequired = 1 + (level / 2);

        return Map.of(
                "wardId", wardId,
                "alertLevel", level,
                "doctorsRequired", doctorsRequired,
                "status", "on-call"
        );
    }
}

