# HealthSafe

## Overview

HealthSafe is a Java-based distributed system designed to manage hospital ward information, emergency alert levels, and staffing requirements.

The project explores how independent services communicate through REST APIs and how asynchronous messaging can be introduced using ActiveMQ.

Development focused on CSV data cleaning, REST service implementation, service integration, and exploring message-based communication.

## Technologies

- Java
- Maven
- Javalin
- OpenCSV
- Jackson
- Java HTTP client
- ActiveMQ Classic
- Docker

## System Architecture

HealthSafe consists of five independent Maven services.

| Service | Port | Responsibility |
|---|---|---|
| [Ingestion Service](ingestion-service/) | 7030 | Reads, cleans, and exposes ward data |
| [Ward Service](ward-service/) | 7031 | Provides ward information to other services |
| [Alert Level Service](alert-level-service/) | 7032 | Manages hospital emergency status |
| [Staffing Service](staffing-service/) | 7033 | Determines staffing requirements |
| [Equipment Alert Service](equipment-alert-service/) | 7034 | Intended to process equipment-failure alerts |

Each service has its own Maven configuration and can be built independently. There is no parent Maven project.

### REST Communication

The intended service communication follows this structure:

```text
               +---------------------+
               |  Ingestion Service  |
               |      Port 7030      |
               |    Cleaned Wards    |
               +----------+----------+
                          |
                          | REST
                          v
               +---------------------+
               |    Ward Service     |
               |      Port 7031      |
               +----------+----------+
                          |
                          | REST
                          v
               +---------------------+
               |   Staffing Service  |
               |      Port 7033      |
               +----------+----------+
                          ^
                          | REST
                          |
               +----------+----------+
               | Alert Level Service |
               |      Port 7032      |
               +---------------------+
```

This illustrates the intended dependencies. Not every service-to-service interaction has been verified end-to-end.

## Implementation Progress

### Stage 1: Data Ingestion and Cleaning

Work completed on the Ingestion Service includes:

- Reading ward records from `wards-outdated.csv`.
- Separating CSV parsing and cleaning responsibilities into `WardCsvReader` and `WardCleaner`.
- Normalizing ward identifiers, wings, and departments.
- Handling invalid and missing bed availability values.
- Processing duplicate ward records.
- Exposing cleaned data through `GET /wards`.

The ingestion endpoint was manually tested and returned JSON ward records.

### Stage 2: REST Services

REST development included the Ward, Alert Level, and Staffing services.

The following endpoints were worked on:

| Service | Endpoint | Status |
|---|---|---|
| Ingestion | `GET /health` | Tested |
| Ingestion | `GET /wards` | Tested |
| Ward | `GET /health` | Tested |
| Ward | Ward lookup endpoints | Not fully verified |
| Alert Level | `GET /alert-level` | Implemented, final behavior needs verification |
| Staffing | `GET /staffing/{wardId}` | Manually tested |

The Staffing Service successfully returned staffing information for ward `W-05`.

Example response observed during development:

```json
{
  "wardId": "W-05",
  "alertLevel": 0,
  "doctorsRequired": 1,
  "status": "on-call"
}
```

A JSON serialization issue was encountered while working with the Alert Level Service, so its final endpoint behavior requires confirmation.

### Stage 3: ActiveMQ Messaging

The project introduces asynchronous communication through an ActiveMQ topic named:

```text
staffing-events-topic
```

The intended design allows the Staffing Service to publish staffing updates and the Ward Service to receive them without relying on direct REST calls for those notifications.

ActiveMQ Classic was started using Docker Compose during development.

The complete message publishing and subscription flow has not been verified end-to-end.

### Stage 4: Equipment Failure Alerting

The intended design uses an ActiveMQ queue:

```text
equipment-failure-queue
```

The Ward Service would publish equipment-failure events, and the Equipment Alert Service would consume them.

This stage was not completed. No application source code was implemented in the Equipment Alert Service as part of this work.

## REST API Reference

| Method | Endpoint | Port | Purpose |
|---|---|---|---|
| GET | `/health` | 7030 | Ingestion health check |
| GET | `/wards` | 7030 | Retrieve cleaned ward records |
| GET | `/health` | 7031 | Ward Service health check |
| GET | `/health` | 7032 | Alert Level Service health check |
| GET | `/alert-level` | 7032 | Retrieve current emergency alert level |
| GET | `/health` | 7033 | Staffing Service health check |
| GET | `/staffing/{wardId}` | 7033 | Retrieve staffing requirements |

Some endpoints are documented from implementation work but require final verification before being considered fully operational.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- Docker with Docker Compose for ActiveMQ
- Git

### Clone the Repository

```bash
git clone <repository-url>
cd sin-000-healthsafe
```

Replace `<repository-url>` with the actual GitHub repository URL.

### Build the Services

Each service is an independent Maven project.

For example:

```bash
cd ingestion-service
mvn clean package
```

Repeat the build command in the other service directories as needed.

### Run the Services

Open a separate terminal for each service.

**Ingestion Service**

```bash
cd ingestion-service
mvn clean package
java -jar target/ingestion-service.jar
```

**Ward Service**

```bash
cd ward-service
mvn clean package
java -jar target/ward-service.jar
```

**Alert Level Service**

```bash
cd alert-level-service
mvn clean package
java -jar target/alert-level-service.jar
```

**Staffing Service**

```bash
cd staffing-service
mvn clean package
java -jar target/staffing-service.jar
```

These commands assume each terminal starts in the project root directory.

### Start ActiveMQ

From the project root:

```bash
docker compose -f common/docker-compose.yml up -d
```

Check the container status:

```bash
docker compose -f common/docker-compose.yml ps
```

Starting the broker does not by itself mean the messaging integration is complete.

## Manual Testing

### Linux / Git Bash

**Check Ingestion Service:**

```bash
curl http://localhost:7030/health
```

**Retrieve cleaned wards:**

```bash
curl http://localhost:7030/wards
```

**Check Ward Service:**

```bash
curl http://localhost:7031/health
```

**Retrieve emergency alert level:**

```bash
curl http://localhost:7032/alert-level
```

**Retrieve staffing information:**

```bash
curl http://localhost:7033/staffing/W-05
```

### Windows PowerShell

**Check Ingestion Service:**

```powershell
curl.exe http://localhost:7030/health
```

**Retrieve cleaned wards:**

```powershell
curl.exe http://localhost:7030/wards
```

**Check Ward Service:**

```powershell
curl.exe http://localhost:7031/health
```

**Retrieve emergency alert level:**

```powershell
curl.exe http://localhost:7032/alert-level
```

**Retrieve staffing information:**

```powershell
curl.exe http://localhost:7033/staffing/W-05
```

The health endpoints are expected to return `OK`.

To run configured automated tests for an individual service:

```bash
mvn test
```

## Design Decisions and Learning Outcomes

### 1. Separation of Concerns

Separating CSV parsing from data cleaning makes the ingestion code easier to maintain and reason about.

### 2. Independent Services

Each HealthSafe service has a specific responsibility and its own Maven configuration.

This demonstrates service boundaries and modular application design.

### 3. REST Integration

REST provides synchronous request-response communication between services.

Working with REST introduced practical challenges around HTTP endpoints, JSON serialization, and dependencies between services.

### 4. Asynchronous Messaging

ActiveMQ introduces a different communication approach using topics and queues.

The project provided an opportunity to explore the difference between synchronous REST requests and asynchronous event-based communication, although the full messaging implementation was not completed.

### 5. Incremental Development

The project was approached through smaller implementation steps, manual verification, and Git commits.

This helped identify integration problems and made individual changes easier to track.

## Known Limitations

- Some REST integrations require further verification.
- The Alert Level Service encountered a JSON mapper issue during development.
- ActiveMQ messaging was not fully verified end-to-end.
- Equipment-failure alerting was not implemented.
- Comprehensive automated test coverage has not been confirmed.
- The project uses a local CSV dataset rather than persistent database storage.

## Project Documentation

Each service has its own README describing its responsibilities and implementation status:

- [Ingestion Service](ingestion-service/README.md)
- [Ward Service](ward-service/README.md)
- [Alert Level Service](alert-level-service/README.md)
- [Staffing Service](staffing-service/README.md)
- [Equipment Alert Service](equipment-alert-service/README.md)
- [ActiveMQ Configuration](common/README.md)

## Conclusion

HealthSafe provided practical experience with data cleaning, REST APIs, service responsibilities, and distributed system integration.

The core learning involved understanding how independent services exchange information, how data is prepared for downstream consumers, and how messaging can reduce direct dependencies between services.

The project remains partially implemented, with further work needed on messaging integration, equipment-failure alerting, and comprehensive testing.
