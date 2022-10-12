package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    int eventId;
    int userId;
    String eventType;
    String operation;
    int entityId;
    long timestamp;

    public Event(int userId, String eventType, String operation) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
    }
}
