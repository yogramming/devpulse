package com.yogramming.devpulse.service;

import com.yogramming.devpulse.model.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final List<Event> events = new ArrayList<>();

    public void addEvent(String message) {
        events.add(new Event(message));
    }

    public List<Event> getEvents() {
        return events;
    }
}
