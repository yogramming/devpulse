package com.yogramming.devpulse.model;

public class Event {
    private String message;
    private long timestamp;

    public Event(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
