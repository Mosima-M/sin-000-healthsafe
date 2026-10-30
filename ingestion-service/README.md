# Ingestion Service

## Overview

The Ingestion Service is responsible for reading, cleaning, and exposing ward data from the legacy `wards-outdated.csv` dataset.

It forms the first stage of the HealthSafe system and provides standardized ward records that other services can retrieve through REST.

The service is an independent Maven module running on port `7030`.

## Responsibilities

The service is designed to:

- Read ward records from the CSV dataset using OpenCSV.
- Remove unnecessary whitespace from field values.
- Normalize ward identifiers and text fields.
- Handle missing, invalid, and non-numeric bed availability values.
- Identify duplicate ward records.
- Expose cleaned ward information through a REST API.

## Dataset

The CSV file contains four fields:

| Field | Description |
|---|---|
| `ward_id` | Unique ward identifier |
| `Wing` | Hospital wing |
| `department` | Medical department |
| `beds_available` | Number of available beds |

### Data Cleaning

The implementation separates CSV reading from data normalization.

- `WardCsvReader` handles reading and parsing the CSV records.
- `WardCleaner` contains the field-cleaning and normalization logic.

The cleaning process addresses inconsistent casing, surrounding whitespace, missing values, and invalid numeric representations.

### Example

**Original CSV record:**

```csv
w-05,east wing ,PAEDIATRICS,five
```

**Illustrative normalized fields:**

```text
Ward ID: W-05
Wing: East Wing
Department: Paediatrics
Beds available: unknown
```

The example demonstrates the intended normalization. The exact output for duplicate records and invalid bed values depends on the implemented cleaning rules.

## REST API

**Base URL:** `http://localhost:7030`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/health` | Checks whether the service is running |
| GET | `/wards` | Returns the cleaned ward records as JSON |

## Technologies

- Java
- Maven
- Javalin
- OpenCSV
- Jackson

## Build and Run

From the `ingestion-service` directory:

**Build:**

```bash
mvn clean package
```

**Run:**

```bash
java -jar target/ingestion-service.jar
```

The service runs on port `7030`.

## Testing

The endpoints can be tested from Linux or Windows.

### Linux / Git Bash

**Health check:**

```bash
curl http://localhost:7030/health
```

**Retrieve cleaned wards:**

```bash
curl http://localhost:7030/wards
```

### Windows PowerShell

**Health check:**

```powershell
curl.exe http://localhost:7030/health
```

**Retrieve cleaned wards:**

```powershell
curl.exe http://localhost:7030/wards
```

The health endpoint should return:

```text
OK
```

The `/wards` endpoint should return the processed ward records in JSON format.

To execute any configured automated tests:

```bash
mvn test
```

## Design Decisions

### Separation of CSV Reading and Cleaning

CSV parsing and data normalization are handled by separate classes.

This makes the cleaning logic easier to maintain, test, and modify independently of file reading.

### Centralized Data Preparation

The Ingestion Service handles data preparation so downstream services can consume standardized records rather than repeatedly cleaning the original CSV.

### REST-Based Data Access

The cleaned records are exposed through an HTTP endpoint, allowing other HealthSafe services to retrieve the data without directly accessing the CSV file.

## Limitations

- The dataset is loaded from a local CSV file rather than a database.
- Invalid or missing bed values require explicit handling and cannot always be converted into valid numbers.
- The service does not provide an API for updating the original CSV dataset.

For the overall architecture, see the [main HealthSafe README](../README.md).