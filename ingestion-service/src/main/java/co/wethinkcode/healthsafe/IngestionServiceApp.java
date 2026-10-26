package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import io.javalin.Javalin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static co.wethinkcode.healthsafe.WardCleaner.normalizeBeds;

public class IngestionServiceApp {

    public static void main(String[] args) throws Exception {
        Javalin app = Javalin.create().start(7030);
        List<Ward> wards =  readCsv();
        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards",ctx ->{
            ctx.json(wards);
        });
    }

    public static List<Ward> readCsv() throws Exception {
        List<Ward> readRows = new ArrayList<>();
        InputStream file = IngestionServiceApp.class.getResourceAsStream("/wards-outdated.csv");
        if (file == null) {
            throw new IllegalStateException("file not found");
        }
        try (InputStreamReader reader = new InputStreamReader(file, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext();
            String[] row = csvReader.readNext();

            Set<String> cleanedRows = new HashSet<>();
            while (row != null) {
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                row[0] = row[0].toUpperCase();
                boolean first = cleanedRows.add(row[0]);
                if (!row[0].isEmpty() && first) {
                    if (!row[1].isEmpty()) {
                        row[1] = WardCleaner.normalizeWing(row[1]);

                        switch (row[1]) {
                            case "northwing" -> row[1] = "North Wing";
                            case "southwing" -> row[1] = "South Wing";
                            case "westwing" -> row[1] = "West Wing";
                            case "eastwing" -> row[1] = "East Wing";
                        }
                    } else {
                        row[1] = "unknown";
                    }

                    row[2] = WardCleaner.normalizeWing(row[2]);
                    if(row[2].equals("Icu")){
                        row[2] = "ICU";
                    }
                    row[3] = normalizeBeds(row[3]);

                   Ward cleanedWard = new Ward(row[0], row[1], row[2], row[3]);
                   readRows.add(cleanedWard);
                }
                row = csvReader.readNext();
            }
        }
        return readRows;
    }
}
