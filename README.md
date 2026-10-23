# CounterDrone

AI-Based Counter-Drone Detection and Threat Management System using Java.

## Current project
- Java 17 and Spring Boot REST API.
- Leaflet/OpenStreetMap dashboard with aircraft markers.
- The dashboard requests aircraft state data from OpenSky Network and refreshes every 30 seconds.
- Dockerfile and Render blueprint included.

## Run locally
Run `mvn spring-boot:run`, then open http://localhost:8080.

## Deploy on Render
Create a Web Service from this repository and select Docker runtime, or use the included `render.yaml` blueprint. Render provides the port through the `PORT` environment variable.

## Important limitation
OpenSky Network is an ADS-B aircraft data source, not a dedicated drone Remote ID feed. This dashboard does not claim to detect all drones or classify real drone threats. Actual drone tracking requires compatible Remote ID receivers or other authorized sensors connected to the backend. External feed access may be rate-limited or temporarily unavailable.