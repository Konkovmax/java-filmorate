package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
