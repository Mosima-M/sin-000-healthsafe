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
        List<Ward> wards =  WardCsvReader.readCsv();
        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards",ctx ->{
            ctx.json(wards);
        });
    }
}
