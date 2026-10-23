package com.skyshield;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
public class AircraftController {
    private final RestClient client = RestClient.builder()
            .baseUrl("https://opensky-network.org/api")
            .build();

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("application", "CounterDrone");
        result.put("status", "online");
        result.put("dataSource", "OpenSky Network ADS-B");
        result.put("importantNote", "ADS-B aircraft are not the same as drone Remote ID detections.");
        result.put("time", Instant.now().toString());
        return result;
    }

    @GetMapping("/api/aircraft")
    public Map<String, Object> aircraft() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", "OpenSky Network ADS-B");
        result.put("retrievedAt", Instant.now().toString());
        try {
            Map<?, ?> payload = client.get()
                    .uri("/states/all")
                    .retrieve()
                    .body(Map.class);
            List<?> states = payload == null ? null : (List<?>) payload.get("states");
            List<Map<String, Object>> tracks = new ArrayList<>();
            if (states != null) {
                for (Object item : states) {
                    if (!(item instanceof List<?> s) || s.size() < 11) continue;
                    Object lon = s.get(5);
                    Object lat = s.get(6);
                    if (!(lon instanceof Number) || !(lat instanceof Number)) continue;
                    Map<String, Object> track = new LinkedHashMap<>();
                    track.put("icao24", s.get(0));
                    track.put("callsign", s.get(1) == null ? "" : s.get(1).toString().trim());
                    track.put("originCountry", s.get(2));
                    track.put("longitude", ((Number) lon).doubleValue());
                    track.put("latitude", ((Number) lat).doubleValue());
                    track.put("altitudeMeters", s.get(7));
                    track.put("velocityMetersPerSecond", s.get(9));
                    track.put("headingDegrees", s.get(10));
                    track.put("onGround", s.get(8));
                    tracks.add(track);
                }
            }
            result.put("count", tracks.size());
            result.put("tracks", tracks);
            result.put("available", true);
        } catch (RestClientException | ClassCastException ex) {
            result.put("available", false);
            result.put("count", 0);
            result.put("tracks", List.of());
            result.put("message", "Live feed is temporarily unavailable. OpenSky may limit anonymous requests or require authentication.");
        }
        result.put("droneRemoteIdAvailable", false);
        result.put("note", "This feed shows ADS-B aircraft only. It does not identify drones or provide drone Remote ID.");
        return result;
    }
}