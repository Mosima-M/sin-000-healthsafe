package co.wethinkcode.healthsafe;

public class WardCleaner {

    public static String normalizeWing(String wing) {
        wing = wing.replaceAll("\\s+", " ").trim();

        String[] words = wing.toLowerCase().split(" ");
        String result = "";

        for (String word : words) {
            if (!word.isEmpty()) {
                result += word.substring(0, 1).toUpperCase()
                        + word.substring(1) + " ";
            }
        }

        return result.trim();
    }

    public static String normalizeBeds(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        String cleaned = value.trim().toLowerCase();

        if (cleaned.equals("five")) {
            return "5";
        }
        if (cleaned.equals("n/a") ||
                cleaned.equals("tbd") ||
                cleaned.equals("unknown")) {
            return "unknown";
        }
        try {
            int number = Integer.parseInt(cleaned);
            if (number < 0) {
                return "invalid";
            }
            return cleaned;

        } catch (NumberFormatException e) {
            return "unknown";
        }
    }
}
