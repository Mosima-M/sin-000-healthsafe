# Ward Service

## Overview

The Ward Service is part of the HealthSafe distributed system. Its purpose is to provide access to hospital ward information, including ward identifiers, wings, departments, and bed availability.

It is designed to consume cleaned ward records from the Ingestion Service and make ward information available to other services through REST.

The service is an independent Maven module running on port `7031`.

## Responsibilities

The Ward Service is responsible for:

- Providing access to ward information.
- Supporting ward lookups required by other HealthSafe services.
- Integrating with the Ingestion Service for cleaned ward data.
- Serving as a source of ward information for the Staffing and Alert Level services.

The project also defines messaging responsibilities for the Ward Service, but these were not fully implemented.

## Service Communication

### REST Integration

The intended REST communication flow is:

```text
       Staffing Service
              |
              | REST
              v
         Ward Service
          Port 7031
              ^
              | REST
              |
     Alert Level Service

         Ward Service
              |
              | REST
              v
      Ingestion Service
          Port 7030
```

The Ingestion Service is responsible for preparing the ward data, while the Ward Service is intended to provide access to that information.

### ActiveMQ Integration

The project specifies two messaging responsibilities:

1. Subscribe to the ActiveMQ topic `staffing-events-topic` to receive staffing-related events.
2. Publish equipment-failure events to the queue `equipment-failure-queue` for processing by the Equipment Alert Service.

These responsibilities form part of the intended architecture. The message subscription and equipment-failure publishing were not completed in the implementation worked on.

## REST API

**Base URL:** `http://localhost:7031`

| Method | Endpoint | Description | Status |
|---|---|---|---|
| GET | `/health` | Checks whether the service is running | Implemented |
| Ward domain endpoints | To be verified | Retrieve ward information | Implementation not confirmed |

### Health Check

```http
GET http://localhost:7031/health
```

Expected response:

```text
OK
```

The exact domain endpoint paths should be confirmed against the current `WardServiceApp` implementation before documenting them as available.

## Technologies

- Java
- Maven
- Javalin
- REST APIs
- ActiveMQ Classic and JMS (planned messaging integration)

## Build and Run

From the `ward-service` directory:

**Build:**

```bash
mvn clean package
```

**Run:**

```bash
java -jar target/ward-service.jar
```

The service listens on port `7031`.

## Testing

### Linux / Git Bash

```bash
curl http://localhost:7031/health
```

### Windows PowerShell

```powershell
curl.exe http://localhost:7031/health
```

Expected response:

```text
OK
```

To run any configured automated tests:

```bash
mvn test
```

A successful health check confirms that the application is running. It does not independently verify ward lookups or ActiveMQ integration.

## Design Decisions

### Separation of Responsibilities

Ward data cleaning is handled by the Ingestion Service, while the Ward Service is intended to expose ward information to other services.

This avoids duplicating CSV-processing logic.

### REST Communication

REST provides a request-response mechanism for services that need ward information immediately.

### Messaging Architecture

ActiveMQ was selected for the later integration stage to support asynchronous communication between services.

The intended use of a topic for staffing events and a queue for equipment failures demonstrates two different messaging patterns.

## Known Limitations

- The complete ward-domain REST functionality has not been verified for this documentation update.
- The ActiveMQ staffing-event subscription was not completed.
- Equipment-failure publishing to the queue was not completed.
- The service's integration with downstream consumers requires further end-to-end verification.

For the overall architecture and implementation status, see the [main HealthSafe README](../README.md).