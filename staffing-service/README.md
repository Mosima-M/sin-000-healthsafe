# Staffing Service

## Overview

The Staffing Service is responsible for determining hospital staffing requirements based on ward information and the current emergency alert level.

It forms part of the HealthSafe distributed system and is designed to communicate with the Ward Service and Alert Level Service through REST APIs.

The service is an independent Maven module running on port `7033`.

## Responsibilities

- Provide staffing information for a requested ward.
- Use the current hospital alert level when determining staffing requirements.
- Return the number of doctors required and the staffing status.
- Integrate with other HealthSafe services through REST.
- Support asynchronous staffing notifications through ActiveMQ as part of the intended architecture.

## REST API

**Base URL:** `http://localhost:7033`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/health` | Checks whether the service is running |
| GET | `/staffing/{wardId}` | Returns staffing requirements for the specified ward |

### Example

**Request:**

```http
GET http://localhost:7033/staffing/W-05
```

**Response observed during development:**

```json
{
  "wardId": "W-05",
  "alertLevel": 0,
  "doctorsRequired": 1,
  "status": "on-call"
}
```

The response demonstrates the staffing information returned for ward `W-05` at alert level `0`.

## Service Communication

The Staffing Service is designed to retrieve information from two other services.

```text
      Ward Service          Alert Level Service
        Port 7031                Port 7032
             |                       |
             | REST                  | REST
             v                       v
             +-----------------------+
                         |
                  Staffing Service
                     Port 7033
                         |
                         v
                 Staffing Response
```

The Ward Service provides ward information, while the Alert Level Service provides the current emergency status.

The Staffing Service uses this information to determine staffing requirements.

## ActiveMQ Integration

The project specifies that the Staffing Service should publish staffing-related events to the ActiveMQ topic:

```text
staffing-events-topic
```

The Ward Service is intended to subscribe to this topic.

This demonstrates the publish-subscribe messaging pattern, where a publisher sends events without directly calling each subscriber.

**Implementation status:** The messaging functionality has not been confirmed as complete or successfully tested end-to-end.

## Technologies

- Java
- Maven
- Javalin
- REST APIs
- JSON
- ActiveMQ Classic / JMS (messaging integration)

## Build and Run

From the `staffing-service` directory:

**Build:**

```bash
mvn clean package
```

**Run:**

```bash
java -jar target/staffing-service.jar
```

The service listens on port `7033`.

## Testing

### Linux / Git Bash

**Health check:**

```bash
curl http://localhost:7033/health
```

**Retrieve staffing information:**

```bash
curl http://localhost:7033/staffing/W-05
```

### Windows PowerShell

**Health check:**

```powershell
curl.exe http://localhost:7033/health
```

**Retrieve staffing information:**

```powershell
curl.exe http://localhost:7033/staffing/W-05
```

The health endpoint is expected to return:

```text
OK
```

The staffing endpoint should return a JSON response containing the ward identifier, alert level, doctors required, and staffing status.

To run any configured automated tests:

```bash
mvn test
```

## Design Decisions

### Separation of Responsibilities

The Staffing Service focuses on staffing requirements rather than managing ward records or emergency alert levels.

This allows each service to handle its own responsibilities.

### REST-Based Integration

REST communication enables the Staffing Service to request ward and emergency-status information from the relevant services.

### Event-Driven Communication

The planned ActiveMQ integration introduces asynchronous messaging for staffing events.

Using a topic would allow multiple interested services to receive staffing updates without requiring direct REST calls from the publisher.

## Known Limitations

- Staffing data was manually verified for ward `W-05`, but comprehensive automated testing has not been confirmed.
- ActiveMQ event publishing has not been verified end-to-end.
- Staffing calculations depend on the availability and correct responses of other services.
- Persistent storage of staffing information has not been confirmed.

For the overall architecture, see the [main HealthSafe README](../README.md).