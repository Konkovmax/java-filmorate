package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventDbStorage implements EventStorage{

    JdbcTemplate jdbcTemplate;

    @Autowired
    EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Event event) {
        if (event == null)
            return;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlEvents = "INSERT INTO EVENTS (USERID, EVENTTYPEID, OPERATIONID) " +
                "VALUES (?, " +
                "        (select EVENTTYPEID from EVENTTYPES where EVENTTYPE=?), " +
                "        (select OPERATIONID from OPERATIONS where OPERATION=?))";
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
                sqlEntity = "INSERT INTO EVENT_LIKE (EVENTID, FILMID) VALUES (?, ?)";
                break;
            case "REVIEW":
                sqlEntity = "INSERT INTO EVENT_REVIEW (EVENTID, REVIEWID) VALUES (?, ?)";
                break;
            case "FRIEND":
                sqlEntity = "INSERT INTO EVENT_FRIEND (EVENTID, FRIENDID) VALUES (?, ?)";
                break;
        }
        System.out.println("##################### ADD #" + event + "###########################");
        jdbcTemplate.update(sqlEntity, event.getEventId(), event.getEntityId());
    }

    @Override
    public List<Event> getAllEvents(int userId) {
        String sql = "select E.EVENTID, E.USERID, ET.EVENTTYPE, OP.OPERATION, EL.FILMID as FILMID, " +
                "       ER.REVIEWID as REVIEWID, EF.FRIENDID as FRIENDID " +
                "from EVENTS as E " +
                "left join EVENT_LIKE as EL on E.EVENTID = EL.EVENTID " +
                "left join EVENT_REVIEW as ER on E.EVENTID = ER.EVENTID " +
                "left join EVENT_FRIEND as EF on E.EVENTID = EF.EVENTID " +
                "join EVENTTYPES as ET on E.EVENTTYPEID = ET.EVENTTYPEID " +
                "join OPERATIONS as OP on E.OPERATIONID = OP.OPERATIONID " +
                "where USERID=?";

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
        System.out.println("##################### GET #" + event + "###########################");
        return event;
    }
}
