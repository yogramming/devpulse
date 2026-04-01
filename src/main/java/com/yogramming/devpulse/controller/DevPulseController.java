package com.yogramming.devpulse.controller;

import com.yogramming.devpulse.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class DevPulseController {

    private final EventService service;
    private final Random random = new Random();

    public DevPulseController(EventService service) {
        this.service = service;
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "DevPulse is running");
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/event")
    public String addEvent(@RequestBody Map<String, String> body) {
        service.addEvent(body.get("message"));
        return "Event added";
    }

    @GetMapping("/events")
    public List<?> getEvents() {
        return service.getEvents();
    }

    @GetMapping("/fail")
    public String fail() throws InterruptedException {
        int n = random.nextInt(3);

        if (n == 0) {
            return "All good!";
        } else if (n == 1) {
            Thread.sleep(3000);
            return "Slow response...";
        } else {
            throw new RuntimeException("Simulated failure!");
        }
    }
}
