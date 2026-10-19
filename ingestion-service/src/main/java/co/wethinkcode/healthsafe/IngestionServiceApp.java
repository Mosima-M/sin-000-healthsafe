package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.javalin.Javalin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IngestionServiceApp {

    public static void main(String[] args) throws Exception {
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        readCsv();
    }

    private static String normalizeWing(String wing) {
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


    public static void readCsv() throws Exception {
        InputStream file = IngestionServiceApp.class.getResourceAsStream("/wards-outdated.csv");
        if (file == null) {
            throw new IllegalStateException("file not found");
        }
        try (InputStreamReader reader = new InputStreamReader(file, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext();
            String[] row = csvReader.readNext();

            Set<String> cleanedRows = new HashSet<>();
            List<String> readRows = new ArrayList<>();
            while (row != null) {
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                row[0] = row[0].toUpperCase();
                boolean first = cleanedRows.add(row[0]);
                if (!row[0].isEmpty() && first) {
                    if (!row[1].isEmpty()) {
                        row[1] = normalizeWing(row[1]);

                        switch (row[1]) {
                            case "northwing" -> row[1] = "North Wing";
                            case "southwing" -> row[1] = "South Wing";
                            case "westwing" -> row[1] = "West Wing";
                            case "eastwing" -> row[1] = "East Wing";
                        }
                    } else {
                        row[1] = "unknown";
                    }
                    row[2] = normalizeWing(row[2]);


                    readRows.add(Arrays.toString(row));

                }
                row = csvReader.readNext();
            }
            System.out.println(readRows);

        }
    }
}
