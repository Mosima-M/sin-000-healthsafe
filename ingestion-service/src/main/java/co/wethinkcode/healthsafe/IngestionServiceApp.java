package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.javalin.Javalin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IngestionServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        InputStream file = IngestionServiceApp.class.getResourceAsStream("/wards-outdated.csv");
        if (file == null) {
            throw new IllegalStateException("file not found");
        }
        try (InputStreamReader reader = new InputStreamReader(file, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext();
            String[] row = csvReader.readNext();
            while (row != null){
                System.out.println(Arrays.toString(row));
                row = csvReader.readNext();
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
