package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDBStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    public List<Film> recommendations(int userId) {
        List<Film> recommendations = new ArrayList<>();
        String createQuery = "SELECT filmid FROM likes WHERE usersid = ? ";
        List<Integer> filmsForUserId = jdbcTemplate.queryForList(createQuery, Integer.class, userId);
        if (filmsForUserId.size() < 1) {
            return recommendations;
        }
        createQuery = "SELECT usersid FROM(SELECT usersid, COUNT(filmid) AS commonfilm " +
                "FROM likes " +
                "WHERE usersid != ? " +
                "AND filmid IN (SELECT filmid FROM likes WHERE usersid = ?) " +
                "GROUP BY usersid " +
                "ORDER BY commonfilm DESC " +
                "LIMIT 1) ";

        List<Integer> otherUserIds = jdbcTemplate.queryForList(createQuery, Integer.class, userId, userId);
        if (otherUserIds.size() < 1) {
            return recommendations;
        }
        int otherUserId = otherUserIds.get(0);

        String createQueryRecommendation = "SELECT filmid FROM likes " +
                "WHERE usersid = ? AND " +
                "filmid NOT IN (SELECT filmid FROM likes WHERE usersid = ?) ";

        List<Integer> filmsId = jdbcTemplate.queryForList(createQueryRecommendation, Integer.class, otherUserId, userId);

        for (int idFilm : filmsId) {
            Optional<Film> film = filmStorage.getFilm(idFilm);
            if (film.isPresent()) {
                recommendations.add(filmStorage.getFilm(idFilm).get());
            }
        }
        return recommendations;
    }
}
