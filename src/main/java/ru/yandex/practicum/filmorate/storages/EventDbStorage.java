package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventDbStorage implements EventStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Event event) {
        if (event == null) {
            return;
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlEvents = "INSERT INTO events (userid, eventtypeid, operationid) " +
                "VALUES (?, " +
                "        (SELECT eventtypeid FROM eventtypes WHERE eventtype=?), " +
                "        (SELECT operationid FROM operations WHERE operation=?))";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlEvents, new String[]{"EVENTID"});
            ps.setInt(1, event.getUserId());
            ps.setString(2, event.getEventType());
            ps.setString(3, event.getOperation());
            return ps;
        }, keyHolder);
        event.setEventId(keyHolder.getKey().intValue());

        String sqlEntity = "";
        switch (event.getEventType()) {
            case "LIKE":
                sqlEntity = "INSERT INTO event_like (eventid, filmid) VALUES (?, ?)";
                break;
            case "REVIEW":
                sqlEntity = "INSERT INTO event_review (eventid, reviewid) VALUES (?, ?)";
                break;
            case "FRIEND":
                sqlEntity = "INSERT INTO event_friend (eventid, friendid) VALUES (?, ?)";
                break;
        }
        jdbcTemplate.update(sqlEntity, event.getEventId(), event.getEntityId());
    }

    @Override
    public List<Event> getAllEvents(int userId) {
        String sql = "SELECT e.eventid, e.userid, e.timestamp, et.eventtype, op.operation, el.filmid AS filmid, " +
                "       er.reviewid AS reviewid, ef.friendid AS friendid " +
                "FROM events AS e " +
                "LEFT JOIN event_like AS el ON e.eventid = el.eventid " +
                "LEFT JOIN event_review AS er ON e.eventid = er.eventid " +
                "LEFT JOIN event_friend AS ef ON e.eventid = ef.eventid " +
                "JOIN eventtypes AS et ON e.eventtypeid = et.eventtypeid " +
                "JOIN operations AS op ON e.operationid = op.operationid " +
                "WHERE userid=?";

        List<Event> events = jdbcTemplate.query(sql, this::makeEvent, userId);

        return events;
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event(rs.getInt("USERID"), rs.getString("EVENTTYPE"),
                rs.getString("OPERATION"));
        event.setEventId(rs.getInt("EVENTID"));
        int entityId = 0;
        if ("LIKE".equals(event.getEventType()))
            entityId = rs.getInt("FILMID");
        else if ("REVIEW".equals(event.getEventType()))
            entityId = rs.getInt("REVIEWID");
        else if ("FRIEND".equals(event.getEventType()))
            entityId = rs.getInt("FRIENDID");
        event.setEntityId(entityId);
        var date = rs.getTimestamp("TIMESTAMP").getTime();
        event.setTimestamp(date);
        return event;
    }
}
