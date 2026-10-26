
package co.wethinkcode.healthsafe;

public class AlertLevelService {

    private int level = 0;

    public int getLevel() {
        return level;
    }

    public void updateLevel(int newLevel) {
        if (newLevel < 0 || newLevel > 8) {
            throw new IllegalArgumentException(
                    "Alert level must be between 0 and 8"
            );
        }
        level = newLevel;
    }
}

