# Alert Level Service

## Overview

The Alert Level Service is responsible for managing the hospital's emergency alert level in the HealthSafe system.

Alert levels range from `0` to `8`, with `8` representing a full Code Blue emergency.

The service is designed to provide the current alert level to other services, particularly the Staffing Service, which uses it when determining staffing requirements.

This is an independent Maven module running on port `7032`.

## Responsibilities

- Maintain the current hospital emergency alert level.
- Expose alert-level information through a REST API.
- Provide alert-level data to the Staffing Service.
- Support changes in staffing requirements as the hospital's emergency status changes.

## REST API

**Base URL:** `http://localhost:7032`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/health` | Checks whether the service is running |
| GET | `/alert-level` | Retrieves the current emergency alert level |

The `/alert-level` endpoint was part of the implementation work, but its successful operation should be verified because a JSON serialization issue was encountered during development.

## Service Communication

The Alert Level Service uses REST communication to provide information to the Staffing Service.

```text
    Alert Level Service
         Port 7032
             |
             | REST response
             v
      Staffing Service
         Port 7033
             |
             v
   Staffing Requirements
```

The Staffing Service can use the current alert level when calculating staffing requirements for a particular ward.

## Technologies

- Java
- Maven
- Javalin
- REST APIs
- JSON serialization

## Build and Run

From the `alert-level-service` directory:

**Build:**

```bash
mvn clean package
```

**Run:**

```bash
java -jar target/alert-level-service.jar
```

The service listens on port `7032`.

## Testing

### Linux / Git Bash

**Health check:**

```bash
curl http://localhost:7032/health
```

**Retrieve alert level:**

```bash
curl http://localhost:7032/alert-level
```

### Windows PowerShell

**Health check:**

```powershell
curl.exe http://localhost:7032/health
```

**Retrieve alert level:**

```powershell
curl.exe http://localhost:7032/alert-level
```

The expected health-check response is:

```text
OK
```

The alert-level endpoint should return the current emergency status, subject to successful JSON serialization.

To run any configured automated tests:

```bash
mvn test
```

## Design Decisions

### Centralized Emergency Status

Keeping the hospital's alert level in a dedicated service allows other services to retrieve a consistent emergency status instead of maintaining separate values.

### REST-Based Communication

REST provides a straightforward way for the Staffing Service to request the current alert level when needed.

### Separation of Responsibilities

The Alert Level Service manages emergency status, while the Staffing Service handles staffing calculations.

This keeps each service focused on a specific responsibility.

## Known Limitations

- A JSON mapper issue was encountered when testing the `/alert-level` endpoint.
- The final behavior of this endpoint requires verification.
- Persistence of alert-level changes has not been confirmed.
- Complete integration testing with the Staffing Service has not been confirmed.

For the overall system architecture, see the [main HealthSafe README](../README.md).