# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W - Client-Server Architectures  
**Student Package:** `com.wmin.smartcampus`  
**Technology:** JAX-RS (Jersey 2.41) · Maven Web Application (WAR)  
**Base Path:** `/api/v1`  
**GitHub:** https://github.com/roomadu/smart-campus-api

---

## API Design Overview

| Endpoint | Method | Description |
|---|---|---|
| `/api/v1` | GET | Discovery - metadata, versioning & resource links |
| `/api/v1/rooms` | GET | List all campus rooms |
| `/api/v1/rooms` | POST | Create a new room (201 + Location header) |
| `/api/v1/rooms/{roomId}` | GET | Get room details |
| `/api/v1/rooms/{roomId}` | DELETE | Decommission room (blocked if sensors exist, 409) |
| `/api/v1/sensors` | GET | List sensors (optional ?type= filter) |
| `/api/v1/sensors` | POST | Register sensor (validates roomId, 422 if missing) |
| `/api/v1/sensors/{sensorId}` | GET | Get sensor details |
| `/api/v1/sensors/{sensorId}/readings` | GET | Reading history |
| `/api/v1/sensors/{sensorId}/readings` | POST | Record new reading (blocked if MAINTENANCE, 403) |

**Business Constraints:**
- Room deletion is blocked when sensors are still linked (409 Conflict)
- Sensor creation validates the roomId exists (422 Unprocessable Entity)
- Reading POST is blocked if sensor status is MAINTENANCE (403 Forbidden)
- Successful reading POST updates parent sensor currentValue as a side-effect

---

## Build & Run (No Server Installation Required)

```bash
# 1. Clone the repository
git clone https://github.com/roomadu/smart-campus-api
cd smart-campus-api

# 2. Run with embedded Jetty (no Tomcat/GlassFish needed)
mvn jetty:run
```

The API will start automatically on port 8080. Access it at:
```
http://localhost:8080/smart-campus-api/api/v1
```

To stop the server press `Ctrl + C`.

> **Alternative:** If you prefer to deploy to a server manually, run `mvn clean package` and deploy `target/smart-campus-api.war` to any Tomcat 9 or GlassFish 5 instance.

---

## Sample curl Commands

```bash
# Discovery
curl -s http://localhost:8080/smart-campus-api/api/v1

# List rooms
curl -s http://localhost:8080/smart-campus-api/api/v1/rooms

# Create room
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"roomId":"ROOM-010","name":"Data Centre","building":"Server Hall","floor":0,"type":"OFFICE","capacity":5}'

# Get room
curl -s http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM-001

# Delete room with no sensors (returns 204)
curl -s -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM-003

# Delete room that has sensors (returns 409 Conflict)
curl -s -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM-001

# Register sensor (valid roomId)
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"SENS-HUM-01","type":"HUMIDITY","roomId":"ROOM-002","status":"ACTIVE","currentValue":55.0,"unit":"%"}'

# Register sensor with invalid roomId (returns 422)
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"type":"CO2","roomId":"ROOM-INVALID","status":"ACTIVE"}'

# Filter sensors by type
curl -s "http://localhost:8080/smart-campus-api/api/v1/sensors?type=TEMPERATURE"

# Post reading (valid)
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/SENS-TEMP-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.5}'

# Post reading to a sensor in MAINTENANCE status (returns 403 Forbidden)
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/SENS-TEMP-02/readings \
  -H "Content-Type: application/json" \
  -d '{"value":18.0}'

# Get reading history
curl -s http://localhost:8080/smart-campus-api/api/v1/sensors/SENS-TEMP-01/readings
```

---

## Conceptual Report

### Part 1.1 - JAX-RS Resource Lifecycle

**Q: Explain the default lifecycle of a JAX-RS Resource class. How does this impact in-memory data management?**

By default, JAX-RS resource classes are **request-scoped** - the runtime creates a new instance for every incoming HTTP request. This means instance fields are not preserved between requests. Because our application uses in-memory data structures (`ConcurrentHashMap`) as the persistence layer, these must be held in **static fields** within the `DataStore` class. Static fields exist at the class level in the JVM, not the instance level, so they survive across all request instances. Furthermore, `ConcurrentHashMap` is used instead of a plain `HashMap` to prevent race conditions when multiple concurrent requests attempt to read/write the store simultaneously, ensuring thread-safe operations without explicit synchronisation blocks.

---

### Part 1.2 - HATEOAS

**Q: Why is hypermedia (HATEOAS) considered a hallmark of advanced RESTful design?**

HATEOAS (Hypermedia As The Engine Of Application State) embeds navigational links directly in API responses. Instead of the client hard-coding URLs, it discovers them from the response. For example, our `GET /api/v1` response includes links to `/api/v1/rooms` and `/api/v1/sensors`. The benefits are: (1) **Reduced coupling** - client code does not break when URL paths change server-side; (2) **Self-documenting** - a developer exploring the API can navigate it purely by following links; (3) **Evolvability** - new endpoints can be introduced without updating clients who follow links dynamically.

---

### Part 2.1 - Room ID vs Full Object

**Q: Returning only IDs versus full room objects?**

Returning **only IDs** reduces payload size significantly, which matters on constrained networks, but forces the client to issue `N` additional GET requests to fetch room details (the N+1 problem), increasing total latency and server load. Returning **full objects** in a single response increases individual payload size but eliminates extra round-trips, making it preferable for most use cases. For our campus dashboard which renders room cards immediately, returning full objects is the correct choice.

---

### Part 2.2 - DELETE Idempotency

**Q: Is DELETE idempotent in your implementation?**

Yes. Idempotency means repeated identical requests produce the same server state. The first `DELETE /rooms/{id}` removes the room (returns 204). A second identical call finds no room and returns 404. The **server state after both calls is identical** - the room does not exist - making the operation idempotent by the REST specification. The response code differs (204 then 404), but the state effect is the same.

---

### Part 3.1 - @Consumes Mismatch

**Q: What happens when a client sends data in a different format?**

JAX-RS performs **content negotiation** by examining the `Content-Type` header. If a client sends `text/plain` to an endpoint annotated with `@Consumes(MediaType.APPLICATION_JSON)`, the JAX-RS runtime finds no compatible `MessageBodyReader` and immediately returns **415 Unsupported Media Type** - before the resource method is even invoked. This is a framework-level safeguard that keeps business logic clean.

---

### Part 3.2 - QueryParam vs PathParam for Filtering

**Q: Why use query parameters for filtering instead of path segments?**

Query parameters (`/sensors?type=CO2`) represent **modifiers on a collection query**, not a distinct resource identity. They are: (1) **Optional** - the endpoint works with or without them; (2) **Composable** - multiple filters combine naturally (`?type=CO2&status=ACTIVE`); (3) **Semantically correct** - a URL path should identify a resource, not describe a filter criterion. Path parameters (`/sensors/type/CO2`) would incorrectly imply that `type/CO2` is a specific resource, breaking REST resource design conventions.

---

### Part 4.1 - Sub-Resource Locator Pattern

**Q: Benefits of the Sub-Resource Locator pattern?**

The sub-resource locator pattern delegates `/sensors/{id}/readings` to a separate `SensorReadingResource` class. This enforces **Separation of Concerns**: sensor management logic stays in `SensorResource`, while reading history logic lives in its own focused class. In large APIs with dozens of nested paths, this prevents "God Object" controllers with hundreds of methods. Each class becomes independently testable, maintainable, and readable, reflecting good object-oriented design.

---

### Part 5.2 - 422 vs 404

**Q: Why is 422 more accurate than 404 for a missing referenced resource?**

A `404 Not Found` indicates the **request target URL** does not exist. However, when a sensor POST arrives at the valid endpoint `/api/v1/sensors` with a body referencing a non-existent `roomId`, the endpoint itself exists - the problem is a **semantic validation failure in the payload**. `422 Unprocessable Entity` explicitly communicates that the request was syntactically well-formed but semantically invalid, giving client developers a much more precise error to act on.

---

### Part 5.4 - Stack Trace Security Risks

**Q: Cybersecurity risks of exposing stack traces?**

Stack traces are a reconnaissance goldmine for attackers. They reveal: (1) **Package and class names** - exposing the application's internal architecture; (2) **Library versions** - allowing attackers to cross-reference known CVEs (e.g., a vulnerable Jackson or Jersey version); (3) **File system paths** - revealing server directory structure; (4) **Business logic flow** - showing exactly which code paths exist, aiding injection and exploitation attempts. Our `GlobalExceptionMapper` intercepts all `Throwable` instances and returns a generic 500 response, completely hiding all internals.

---

### Part 5.5 - Why Filters for Logging?

**Q: Why use JAX-RS filters instead of manual logging in resource methods?**

Filters implement **cross-cutting concerns** centrally. The alternative - inserting `Logger.info()` in every resource method - introduces: (1) **Duplication** - dozens of log statements across every method; (2) **Inconsistency** - new endpoints added by team members may omit logging; (3) **Coupling** - business logic is mixed with observability concern. A single `ContainerRequestFilter`/`ContainerResponseFilter` implementation guarantees **uniform, automatic** logging for every current and future endpoint with zero modification to resource classes.

---

## Video Demonstration
**Link:** https://drive.google.com/file/d/1IjR61r_ukXjiUYGA4JcSRi_x0RrRbg6L/view?usp=drive_link
